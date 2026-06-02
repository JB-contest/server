package com.example.jb.document.domain;

public enum DocumentStatus {
	SUBMITTED("검토 대기"),
	AI_VALIDATING("AI 검증중"),
	IN_REVIEW("검토중"),
	REVISION_REQUESTED("수정 요청"),
	APPROVED("승인완료");

	private final String label;

	DocumentStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
