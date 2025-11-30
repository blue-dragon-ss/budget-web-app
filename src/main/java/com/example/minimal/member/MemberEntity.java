package com.example.minimal.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class MemberEntity {
  @Id
  @Setter
  @Column(length = 26)
  private String id; // ULID(26)

  @Setter
  @Column(length = 50, nullable = false)
  private String code;

  @Setter
  @Column(length = 200, nullable = false)
  private String name;

  @Setter
  @Column(length = 320)
  private String email;

  @Setter
  @Column(columnDefinition = "text")
  private String note;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
