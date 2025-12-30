package com.example.minimal.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P203ResponseError {
	int line;
	String code;
	String message;
}
