package com.example.minimal.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	// カテゴリ主キーの存在確認
	boolean existsByPkAndDeletedAtIsNull(Long id);

	// 会員IDとカテゴリローカルIDで存在確認
	Optional<CategoryEntity> findByIdAndMemberIdAndDeletedAtIsNull(Integer id, String memberId);

	// 会員IDで検索
	@Query("""
			    select c
			    from CategoryEntity c
			    left join fetch c.upperCategory u
			    where c.memberId = :memberId
			      and c.deletedAt is null
			    order by c.displayOrder asc, c.id asc, c.pk asc
			""")
	List<CategoryEntity> findActiveWithUpperByMemberId(@Param("memberId") String memberId);
}
