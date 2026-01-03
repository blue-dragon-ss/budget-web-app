package com.example.minimal.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.minimal.category.dto.P301Response;
import com.example.minimal.category.dto.P301ResponseCategory;
import com.example.minimal.common.constants.FixedMemberId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// 明細カテゴリ取得
	@Transactional(readOnly = true)
	public P301Response getCategories() {
		List<CategoryEntity> categories = categoryRepository
				.findActiveWithUpperByMemberId(FixedMemberId.FIXED_MEMBER_ID);
		return toP301Response(categories);
	}

	// Entityをレスポンスに変換
	private P301Response toP301Response(List<CategoryEntity> categories) {
		P301Response res = new P301Response();
		List<P301ResponseCategory> list = new ArrayList<P301ResponseCategory>();
		for (CategoryEntity c : categories) {
			list.add(toP301ResponseCategory(c));
		}
		if (list.size() > 0) {
			res.setCategoryList(list);
		}
		return res;
	}

	// Entityのカテゴリをレスポンスのカテゴリに変換
	private P301ResponseCategory toP301ResponseCategory(CategoryEntity category) {
		P301ResponseCategory res = new P301ResponseCategory();
		res.setCategoryId(category.getId());
		res.setCategoryName(category.getName());
		res.setUpperCategoryID((category.getUpperCategory() == null) ? null : category.getUpperCategory().getId()); // 上位カテゴリをローカルIDに変更
		return res;
	}
}
