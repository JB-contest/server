package com.example.jb.document.dto.request;

import com.example.jb.document.domain.LoanType;
import com.example.jb.document.domain.UploadLocation;

public record DocumentCreateRequest(
		Long uploaderId,
		String name,
		UploadLocation uploadLocation,
		LoanType loanType,
		String title,
		String content
) {
}
