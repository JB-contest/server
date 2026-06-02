package com.example.jb.validation.domain;

import java.util.List;

public record ChecklistItem(
		int id,
		String type,
		String description,
		String keyword,
		String riskLevel,
		List<String> violationTypes,
		List<String> lawMappings
) {
}
