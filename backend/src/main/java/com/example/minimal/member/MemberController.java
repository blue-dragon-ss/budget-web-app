// src/main/java/com/example/minimal/api/member/MemberController.java
package com.example.minimal.member;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.ApiPaths;
import com.example.minimal.member.dto.CreateMemberRequest;
import com.example.minimal.member.dto.MemberResponse;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.MEMBERS_BASE)
public class MemberController {

  private final MemberService memberService;

  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping(ApiPaths.CREATE)
  public ResponseEntity<MemberResponse> create(
      @RequestHeader(value = ApiHeaders.IDEMPOTENCY_KEY, required = false) String idempotencyKey,
      @Valid @RequestBody CreateMemberRequest body) {

    MemberResponse res = memberService.create(body, idempotencyKey);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(res);
  }
}
