package com.example.jb.approval.dto.response;

import java.time.LocalDateTime;

public record ApprovalResponse(
		Long id,
		Long documentId,
		Long approverId,
		String approverName,
		String reviewNumber,
		LocalDateTime approvedAt
) {
}
