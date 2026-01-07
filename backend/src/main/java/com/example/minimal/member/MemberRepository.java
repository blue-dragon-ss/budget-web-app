package com.example.minimal.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
	// コードで存在確認
	boolean existsByCodeAndDeletedAtIsNull(String code);

	// IDで取得（論理削除されていないもの）
	Optional<MemberEntity> findByIdAndDeletedAtIsNull(String id);
}
