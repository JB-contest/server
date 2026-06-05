package com.example.jb.validation.domain;

import java.util.List;

public record ChecklistItem(
		int id,
		String type,
		String description,
		List<String> keywords,
		String riskLevel,
		List<String> violationTypes,
		List<String> lawMappings
) {
}
