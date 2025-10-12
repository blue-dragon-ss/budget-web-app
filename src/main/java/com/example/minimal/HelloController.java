package com.example.minimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public String hello(@org.springframework.web.bind.annotation.RequestParam(name="fail", required=false) Boolean fail) {
	    if (Boolean.TRUE.equals(fail)) {
	        throw new IllegalArgumentException("invalid parameter: fail=true");
	    }
		return "Hello, Spring Boot";
	}

}
