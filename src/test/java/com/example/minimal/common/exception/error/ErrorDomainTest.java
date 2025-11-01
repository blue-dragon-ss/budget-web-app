package com.example.minimal.common.exception.error;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ErrorDomainTest {

	@Test
	void 各項目の整合性を確認する() {
		ErrorDomain common = ErrorDomain.COMMON;
		assertThat(common).isEqualTo(ErrorDomain.COMMON);

		ErrorDomain member = ErrorDomain.MEMBER;
		assertThat(member).isEqualTo(ErrorDomain.MEMBER);

		ErrorDomain invoice = ErrorDomain.INVOICE;
		assertThat(invoice).isEqualTo(ErrorDomain.INVOICE);

		ErrorDomain payment = ErrorDomain.PAYMENT;
		assertThat(payment).isEqualTo(ErrorDomain.PAYMENT);
	}
}
