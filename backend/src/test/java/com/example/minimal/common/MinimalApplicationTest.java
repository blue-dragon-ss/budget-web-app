package com.example.minimal.common;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import com.example.minimal.MinimalApplication;

public class MinimalApplicationTest {

	@Test
	void コンストラクタ() {
		new MinimalApplication();
	}

	@Test
	void コンテキストが起動する() {
		MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class);
		mocked.when(() -> SpringApplication.run(MinimalApplication.class, new String[] {})).thenReturn(null);
		MinimalApplication.main(new String[] {});
	}
}
