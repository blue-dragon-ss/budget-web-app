package com.example.minimal.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P301ResponseCategory {
	private int categoryId;
	private String categoryName;
	private Integer upperCategoryID;
}
