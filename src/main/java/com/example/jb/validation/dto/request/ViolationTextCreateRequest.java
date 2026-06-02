package com.example.jb.validation.dto.request;

import com.example.jb.validation.domain.RiskLevel;

public record ViolationTextCreateRequest(
		String violationText,
		RiskLevel riskLevel,
		String violationTypes,
		String lawMappings
) {
}
