package com.example.minimal.item.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P203Response {
	private int total;
	private int success;
	private int failed;
	private List<P203ResponseError> errors;
	private String traceId;
}
