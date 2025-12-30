package com.example.minimal.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	boolean existsByPkAndDeletedAtIsNull(Long id);
}
