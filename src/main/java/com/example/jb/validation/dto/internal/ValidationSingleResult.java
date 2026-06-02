package com.example.jb.validation.dto.internal;

import java.util.List;

public record ValidationSingleResult(
		double errorRate,
		List<ViolationItem> violations
) {
}
