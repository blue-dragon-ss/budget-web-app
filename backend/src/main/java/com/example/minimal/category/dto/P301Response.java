package com.example.minimal.category.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P301Response {
	private List<P301ResponseCategory> categoryList;
}
