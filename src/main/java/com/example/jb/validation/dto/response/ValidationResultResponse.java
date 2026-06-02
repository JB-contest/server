package com.example.jb.validation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationResultResponse(
		Long id,
		Long documentId,
		Double errorRate,
		LocalDateTime createdAt,
		List<ViolationTextResponse> violations
) {
}
