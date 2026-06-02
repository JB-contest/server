package com.example.jb.approval.dto.request;

public record ApprovalCreateRequest(
		Long approverId,
		String reviewNumber
) {
}
