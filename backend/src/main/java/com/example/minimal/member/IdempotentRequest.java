package com.example.minimal.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(
  name = "idempotent_requests",
  uniqueConstraints = @UniqueConstraint(columnNames = {"endpoint", "idempotency_key"})
)
@Getter
@NoArgsConstructor
public class IdempotentRequest {
  @Id
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "idempotency_key", nullable = false, length = 200)
  private String idempotencyKey;

  @Setter
  @Column(nullable = false, length = 200)
  private String endpoint;

  @Setter
  @Column(name = "request_hash", nullable = false, length = 64)
  private String requestHash;

  @Setter
  @Column(name = "member_id", length = 26)
  private String memberId;

  @Setter
  @Column(name = "response_body", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode responseBody;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();


}
