package com.example.minimal.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CreateMemberRequest {
  @NotBlank(message = "会員コードは必須です。")
  @Size(min = 1, max = 50, message = "会員コードは1〜50文字で指定してください。")
  private String code;

  @NotBlank(message = "会員名は必須です。")
  @Size(min = 1, max = 200, message = "会員名は1〜200文字で指定してください。")
  private String name;

  @Size(max = 320, message = "メールアドレスは最大320文字です。")
  @Pattern(
    regexp = "^(|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})$",
    message = "メールアドレスの形式が正しくありません。"
  )
  private String email;

  @Size(max = 10000, message = "備考は最大10,000文字です。")
  private String note;
}
