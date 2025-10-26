package com.example.minimal.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponse {
  private String id;
  private String code;
  private String name;
  private String email;
  private String note;
  private String createdAt;
  private String updatedAt;
  private String traceId;
}
