package com.example.minimal.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.minimal.category.dto.P301Response;
import com.example.minimal.common.constants.ApiPaths;

@RestController
@RequestMapping(ApiPaths.CATEGORIES_BASE)
@Validated
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	// 明細カテゴリ取得
	@GetMapping
	public ResponseEntity<P301Response> getCategories() {
		P301Response res = categoryService.getCategories();
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}
}
