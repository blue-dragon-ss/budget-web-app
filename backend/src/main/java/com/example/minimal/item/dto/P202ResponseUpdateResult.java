package com.example.minimal.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P202ResponseUpdateResult {
	private String itemId;
	private boolean status;
	private String message;
}
