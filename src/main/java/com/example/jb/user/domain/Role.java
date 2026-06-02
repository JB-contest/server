package com.example.jb.user.domain;

public enum Role {
	MARKETING("마케팅팀"),
	COMPLIANCE("준법자문가");

	private final String label;

	Role(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
