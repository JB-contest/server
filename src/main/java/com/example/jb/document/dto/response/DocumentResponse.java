package com.example.jb.document.dto.response;

import com.example.jb.document.domain.DocumentStatus;
import com.example.jb.document.domain.LoanType;
import com.example.jb.document.domain.UploadLocation;
import java.time.LocalDateTime;

public record DocumentResponse(
		Long id,
		Long parentId,
		int version,
		Long uploaderId,
		String uploaderName,
		String name,
		UploadLocation uploadLocation,
		LoanType loanType,
		String title,
		String content,
		DocumentStatus status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
