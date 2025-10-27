package com.example.minimal.member;

import com.example.minimal.member.dto.CreateMemberRequest;
import com.example.minimal.member.dto.MemberResponse;
import com.example.minimal.member.dto.CreateMemberRequest.Fields;
import com.example.minimal.common.TraceIdHolder;
import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.ApiPaths;
import com.example.minimal.common.constants.SQLState;
import com.example.minimal.common.exception.DuplicateValueException;
import com.example.minimal.common.exception.IdempotencyConflictException;
import com.example.minimal.common.exception.UnexpectedPersistenceException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;

import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;
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

    final String endpoint = ApiPaths.MEMBERS_BASE + ApiPaths.CREATE;
    final String requestHash = sha256(code + "|" + name + "|" + safe(email) + "|" + safe(note));

    // Idempotency: 先に行を確保（claim）。重複したら既存結果を再利用/判定
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      try {
        IdempotentRequest claim = new IdempotentRequest();
        claim.setEndpoint(endpoint);
        claim.setIdempotencyKey(idempotencyKey);
        claim.setRequestHash(requestHash);
        idemRepo.saveAndFlush(claim);
      } catch (DataIntegrityViolationException dup) {
        // 既に同じ (endpoint,key) が存在 → 再利用 or 誤用（payload不一致）判断
        var hit = idemRepo.findByEndpointAndIdempotencyKey(endpoint, idempotencyKey);
        if (hit.isPresent()) {
          IdempotentRequest ir = hit.get();
          if (!ir.getRequestHash().equals(requestHash)) {
        	  throw new IdempotencyConflictException(ApiHeaders.IDEMPOTENCY_KEY,
        			  ErrorMessage.IDE_DEFFERENT_REQUEST_MESSAGE,
        		      ErrorCode.IDE_VAL_DEFFERENT_REQUEST);
          }
          if (ir.getMemberId() != null) {
            var memberEntity = memberRepository.findByIdAndDeletedAtIsNull(ir.getMemberId())
                .orElseThrow(); // ないはずだが念のため
            return toResponse(memberEntity);
          }
          // 同時実行中などで結果未保存 → 最小実装として409返却（425等に変更可）
          throw new IdempotencyConflictException(ApiHeaders.IDEMPOTENCY_KEY,
              ErrorMessage.IDE_SAME_KEY_RUNNING_MESSAGE, ErrorCode.IDE_VAL_SAME_KEY_RUNNING);
        } else {
          // 防御的（理論上到達しない）
          throw new UnexpectedPersistenceException(
        	  null,
        	  ErrorMessage.COM_SERVER_ERROR_MESSAGE,
        	  ErrorCode.COM_SERVER_ERROR,
			  dup
		  );
        }
      }
    }

    // 一意制チェック（論理削除を除く）
    if (memberRepository.existsByCodeAndDeletedAtIsNull(code)) {
      throw new DuplicateValueException(Fields.code, ErrorMessage.MBR_CONFLICT_CODE, ErrorCode.MBR_VAL_CONFLICT_CODE);
    }

    // 登録
    MemberEntity memberEntity = new MemberEntity();
    memberEntity.setId(UlidCreator.getUlid().toString());
    memberEntity.setCode(code);
    memberEntity.setName(name);
    memberEntity.setEmail(email);
    memberEntity.setNote(note);
    try {
    	memberEntity = memberRepository.save(memberEntity);
    } catch (DataIntegrityViolationException e) {
      if (isUniqueViolation(e, "uq_members_code_active")) {
   	    // DB 側で競合（23505）が起きた場合もクライアント向けに統一
        throw new DuplicateValueException(Fields.code, ErrorMessage.MBR_CONFLICT_CODE, ErrorCode.MBR_VAL_CONFLICT_CODE);
      }
      // 想定外の永続化エラーは自前の500用例外に正規化して再投げ
      throw new UnexpectedPersistenceException(
          null,
          ErrorMessage.COM_SERVER_ERROR_MESSAGE,
          ErrorCode.COM_SERVER_ERROR,
          e
      );
    }

    // Idempotency 結果保存（同一キーの再実行に備える）
    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
      var ir = idemRepo.findByEndpointAndIdempotencyKey(endpoint, idempotencyKey)
          .orElse(null);
      if (ir != null) {
        ir.setMemberId(memberEntity.getId());
        try {
          ir.setResponseBody(objectMapper.valueToTree(toResponse(memberEntity)));
        } catch (Exception ignore) { /* noop */ }
        idemRepo.save(ir);
      }
    }

    return toResponse(memberEntity);
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
  
  private static boolean isUniqueViolation(Throwable t, String constraintName) {
	  for (Throwable cur = t; cur != null; cur = cur.getCause()) {
	    if (cur instanceof ConstraintViolationException cve) {
	      // 1) 制約名での一致
	      String name = cve.getConstraintName();
	      if (name != null && name.equalsIgnoreCase(constraintName)) {
	        return true;
	      }
	      // 2) SQLState（Hibernate が抱える SQLException から取得）
	      String state = (cve.getSQLException() != null)
	          ? cve.getSQLException().getSQLState()
	          : null;
	      if (SQLState.UNIQUE_VIOLATION.getCode().equals(state)) {
	        return true;
	      }
	    }
	  }
	  return false;
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
