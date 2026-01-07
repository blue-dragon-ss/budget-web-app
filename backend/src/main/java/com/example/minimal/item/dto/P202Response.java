package com.example.minimal.item.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P202Response {
	private String yearMonth;
	private int totalNum;
	private List<P202ResponseUpdateResult> updateResultList;
}
