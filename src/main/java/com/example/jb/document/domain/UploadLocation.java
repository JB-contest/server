package com.example.jb.document.domain;

public enum UploadLocation {
	DETAIL_PAGE("상세페이지"),
	CARD_NEWS("카드뉴스"),
	FAQ("FAQ"),
	HOMEPAGE("홈페이지");

	private final String label;

	UploadLocation(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
