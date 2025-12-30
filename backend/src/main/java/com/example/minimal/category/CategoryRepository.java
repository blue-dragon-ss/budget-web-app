package com.example.minimal.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
	boolean existsByIdAndDeletedAtIsNull(Long id);
}
