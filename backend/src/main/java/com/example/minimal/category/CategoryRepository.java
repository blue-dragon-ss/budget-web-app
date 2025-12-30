package com.example.minimal.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	// 会員IDとカテゴリローカルIDで存在確認
	boolean existsByPkAndDeletedAtIsNull(Long id);

	// 会員IDとカテゴリローカルIDで存在確認
	Optional<CategoryEntity> findByIdAndMemberIdAndDeletedAtIsNull(Integer id, String memberId);
}
