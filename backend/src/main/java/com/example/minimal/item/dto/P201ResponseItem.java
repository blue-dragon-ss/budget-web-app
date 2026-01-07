package com.example.minimal.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P201ResponseItem {
	private String itemId;
	private String date;
	private String title;
	private int categoryId;
	private String memo;
	private long amount;
}
