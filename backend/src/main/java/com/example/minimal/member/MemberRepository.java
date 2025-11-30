package com.example.minimal.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
  boolean existsByCodeAndDeletedAtIsNull(String code);
  Optional<MemberEntity> findByIdAndDeletedAtIsNull(String id);
}
