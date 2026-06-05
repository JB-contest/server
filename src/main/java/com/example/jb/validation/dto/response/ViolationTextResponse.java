package com.example.jb.validation.dto.response;

import com.example.jb.validation.domain.RiskLevel;
import java.time.LocalDateTime;

public record ViolationTextResponse(
		Long id,
		Long resultId,
		String violationText,
		String violationReason,
		RiskLevel riskLevel,
		String violationTypes,
		String lawMappings,
		LocalDateTime createdAt
) {
}
