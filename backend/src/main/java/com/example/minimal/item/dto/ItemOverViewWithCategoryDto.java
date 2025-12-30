package com.example.minimal.item.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ItemOverViewWithCategoryDto(
		Long id,
		String publicId,
		String billingYm,
		LocalDate usageDate,
		String title,
		BigDecimal usageAmount,
		String memo,
		Integer categoryLocalId // categories.id（ローカルID）
) {
}