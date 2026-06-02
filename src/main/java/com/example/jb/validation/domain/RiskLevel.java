package com.example.jb.validation.domain;

public enum RiskLevel {
	HIGH("높음"),
	MEDIUM("중간"),
	LOW("낮음");

	private final String label;

	RiskLevel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
