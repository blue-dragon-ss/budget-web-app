package com.example.minimal.item.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P201Response {
	private String yearMonth; // yyyy-MM
	private int totalNum;
	private long totalAmount;
	private List<P201ResponseItem> itemizedList;
}
