package com.example.jb.validation.dto.internal;

import com.example.jb.validation.domain.RiskLevel;
import java.util.List;

public record ViolationItem(
		String violationText,
		RiskLevel riskLevel,
		List<String> violationTypes,
		List<String> lawMappings
) {
}
