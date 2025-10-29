package com.example.minimal.member;

import com.example.minimal.common.TraceIdHolder;
import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.ApiPaths;
import com.example.minimal.common.exception.DuplicateValueException;
import com.example.minimal.common.exception.IdempotencyConflictException;
import com.example.minimal.common.exception.UnexpectedPersistenceException;
import com.example.minimal.common.exception.error.ErrorCode;
import com.example.minimal.common.exception.error.ErrorMessage;
import com.example.minimal.common.util.SQLUtils;
import com.example.minimal.common.util.StringUtils;
import com.example.minimal.member.dto.CreateMemberRequest;
import com.example.minimal.member.dto.CreateMemberRequest.Fields;
import com.example.minimal.member.dto.MemberResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.UlidCreator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.format.DateTimeFormatter;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final IdempotentRequestRepository idemRepo;
  private final ObjectMapper objectMapper;
  private final TransactionTemplate idempotencyClaimTxTemplate;
  private final String UQ_MEMBERS_CODE_ACTIVE = "uq_members_code_active";

  public MemberService(MemberRepository memberRepository,
                       IdempotentRequestRepository idemRepo,
                       ObjectMapper objectMapper,
                       PlatformTransactionManager transactionManager) {
    this.memberRepository = memberRepository;
    this.idemRepo = idemRepo;
    this.objectMapper = objectMapper;
    TransactionTemplate claimTemplate = new TransactionTemplate(transactionManager);
    claimTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    claimTemplate.setReadOnly(false);
    this.idempotencyClaimTxTemplate = claimTemplate;
  }

  @Transactional
  public MemberResponse create(CreateMemberRequest req, String idempotencyKey) {
    // 正規化
    String code = StringUtils.trim(req.getCode());
    String name = StringUtils.trim(req.getName());
    String email = StringUtils.normalizeEmail(req.getEmail());
    String note = req.getNote();

    final String endpoint = StringUtils.normalizeEndpoint(ApiPaths.MEMBERS_BASE + ApiPaths.CREATE);
    final String normalizedIdempotencyKey = StringUtils.normalizeIdempotencyKey(idempotencyKey);
    final String requestHash = StringUtils.sha256(code + "|" + name + "|" + StringUtils.safe(email) + "|" + StringUtils.safe(note));

    // Idempotency: 先に行を確保（claim）。重複したら既存結果を再利用/判定
    ClaimOutcome claimOutcome = null;
    if (normalizedIdempotencyKey != null) {
      claimOutcome = idempotencyClaimTxTemplate.execute(status -> {
        try {
          IdempotentRequest claim = new IdempotentRequest();
          claim.setEndpoint(endpoint);
          claim.setIdempotencyKey(normalizedIdempotencyKey);
          claim.setRequestHash(requestHash);
          idemRepo.saveAndFlush(claim);
          return ClaimOutcome.claimed();
        } catch (DataIntegrityViolationException dup) {
          var hit = idemRepo.findByEndpointAndIdempotencyKey(endpoint, normalizedIdempotencyKey);
          if (hit.isEmpty()) {
            throw new UnexpectedPersistenceException(
                null,
                ErrorMessage.COM_SERVER_ERROR_MESSAGE,
                ErrorCode.COM_SERVER_ERROR,
                dup
            );
          }
          IdempotentRequest ir = hit.get();
          if (!ir.getRequestHash().equals(requestHash)) {
            throw new IdempotencyConflictException(
                ApiHeaders.IDEMPOTENCY_KEY,
                ErrorMessage.IDE_DEFFERENT_REQUEST_MESSAGE,
                ErrorCode.IDE_VAL_DEFFERENT_REQUEST
            );
          }
          if (ir.getMemberId() != null) {
            MemberEntity memberEntity = memberRepository
                .findByIdAndDeletedAtIsNull(ir.getMemberId())
                .orElseThrow(() -> new UnexpectedPersistenceException(
                    null,
                    ErrorMessage.COM_SERVER_ERROR_MESSAGE,
                    ErrorCode.COM_SERVER_ERROR,
                    null
                ));
            return ClaimOutcome.replayed(toResponse(memberEntity));
          }
          return ClaimOutcome.inProgress();
        }
      });
      if (claimOutcome == null) {
        throw new UnexpectedPersistenceException(
            null,
            ErrorMessage.COM_SERVER_ERROR_MESSAGE,
            ErrorCode.COM_SERVER_ERROR,
            null
        );
      }
      if (claimOutcome.isReplay()) {
        return claimOutcome.response();
      }
      if (claimOutcome.isInProgress()) {
        throw new IdempotencyConflictException(
            ApiHeaders.IDEMPOTENCY_KEY,
            ErrorMessage.IDE_SAME_KEY_RUNNING_MESSAGE,
            ErrorCode.IDE_VAL_SAME_KEY_RUNNING
        );
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
      if (SQLUtils.isUniqueViolation(e, UQ_MEMBERS_CODE_ACTIVE)) {
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
    if (normalizedIdempotencyKey != null) {
      var ir = idemRepo.findByEndpointAndIdempotencyKey(endpoint, normalizedIdempotencyKey)
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

  private static final class ClaimOutcome {
    private enum Status {
      CLAIMED,
      REPLAY,
      IN_PROGRESS
    }

    private final Status status;
    private final MemberResponse response;

    private ClaimOutcome(Status status, MemberResponse response) {
      this.status = status;
      this.response = response;
    }

    static ClaimOutcome claimed() {
      return new ClaimOutcome(Status.CLAIMED, null);
    }

    static ClaimOutcome replayed(MemberResponse response) {
      return new ClaimOutcome(Status.REPLAY, response);
    }

    static ClaimOutcome inProgress() {
      return new ClaimOutcome(Status.IN_PROGRESS, null);
    }

    boolean isReplay() {
      return status == Status.REPLAY;
    }

    boolean isInProgress() {
      return status == Status.IN_PROGRESS;
    }

    MemberResponse response() {
      return response;
    }
  }
}
