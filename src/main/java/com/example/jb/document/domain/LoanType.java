package com.example.jb.document.domain;

public enum LoanType {
	CREDIT_LOAN("신용대출"),
	COLLATERAL_LOAN("담보대출"),
	JEONSE_LOAN("전세자금대출");

	private final String label;

	LoanType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
