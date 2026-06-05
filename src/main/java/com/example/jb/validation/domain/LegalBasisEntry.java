package com.example.jb.validation.domain;

import java.util.List;

public record LegalBasisEntry(
		String description,
		String riskLevel,
		List<String> violationTypes,
		List<String> lawMappings
) {
}
