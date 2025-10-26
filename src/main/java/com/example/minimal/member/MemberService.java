package com.example.minimal.member;

import com.example.minimal.member.dto.CreateMemberRequest;
import com.example.minimal.member.dto.MemberResponse;
import com.example.minimal.common.TraceIdHolder;
import com.example.minimal.common.exception.DuplicateValueException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final IdempotentRequestRepository idemRepo;
  private final ObjectMapper objectMapper;

  public MemberService(MemberRepository memberRepository,
                       IdempotentRequestRepository idemRepo,
                       ObjectMapper objectMapper) {
    this.memberRepository = memberRepository;
    this.idemRepo = idemRepo;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public MemberResponse create(CreateMemberRequest req, String idempotencyKey) {
    // 正規化
    String code = trim(req.getCode());
    String name = trim(req.getName());
    String email = normalizeEmail(req.getEmail());
    String note = req.getNote();

    final String endpoint = "/api/v1/members/create";
    final String requestHash = sha256(code + "|" + name + "|" + safe(email) + "|" + safe(note));

    // Idempotency: 同一キーで同一リクエストなら前回の結果を返す
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var hit = idemRepo.findByEndpointAndIdempotencyKey(endpoint, idempotencyKey);
      if (hit.isPresent()) {
        IdempotentRequest ir = hit.get();
        if (ir.getRequestHash().equals(requestHash) && ir.getMemberId() != null) {
          // 直リンク再構成（DB参照してもOK）
          var m = memberRepository.findByIdAndDeletedAtIsNull(ir.getMemberId())
              .orElseThrow(); // ないはずだが念のため
          return toResponse(m);
        }
        // キー衝突（異なる入力）→ 業務的には 409/422でもよいが、ここでは400で返す場合も
        throw new DuplicateValueException("X-Idempotency-Key",
            "同一Idempotency-Keyで異なる内容のリクエストが送信されました。", "BUS-0001");
      }
    }

    // 一意制チェック（論理削除を除く）
    if (memberRepository.existsByCodeAndDeletedAtIsNull(code)) {
      throw new DuplicateValueException("code", "会員コードは既に使用されています。", "VAL-0105");
    }

    // 登録
    MemberEntity m = new MemberEntity();
    m.setId(UlidCreator.getUlid().toString());
    m.setCode(code);
    m.setName(name);
    m.setEmail(email);
    m.setNote(note);
    m = memberRepository.save(m);

    // Idempotency 保存（任意）
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      IdempotentRequest ir = new IdempotentRequest();
      ir.setEndpoint(endpoint);
      ir.setIdempotencyKey(idempotencyKey);
      ir.setRequestHash(requestHash);
      ir.setMemberId(m.getId());
      try {
        ir.setResponseBody(objectMapper.valueToTree(toResponse(m)));
      } catch (Exception ignore) { /* noop */ }
      idemRepo.save(ir);
    }

    return toResponse(m);
  }

  private static String trim(String s) { return s == null ? null : s.trim(); }
  private static String normalizeEmail(String s) {
    if (s == null || s.isBlank()) return null;
    return s.trim().toLowerCase();
  }
  private static String safe(String s) { return s == null ? "" : s; }

  private static String sha256(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : dig) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static MemberResponse toResponse(MemberEntity m) {
    MemberResponse res = new MemberResponse();
    res.setId(m.getId());
    res.setCode(m.getCode());
    res.setName(m.getName());
    res.setEmail(m.getEmail());
    res.setNote(m.getNote());
    res.setCreatedAt(DateTimeFormatter.ISO_INSTANT.format(m.getCreatedAt()));
    res.setUpdatedAt(DateTimeFormatter.ISO_INSTANT.format(m.getUpdatedAt()));
    res.setTraceId(TraceIdHolder.get());
    return res;
  }
}
