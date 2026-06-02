package com.example.jb.feedback.domain;

public enum FeedbackDecision {
	APPROVED("승인"),
	REVISION_REQUESTED("수정요청");

	private final String label;

	FeedbackDecision(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
