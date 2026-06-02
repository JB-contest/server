package com.example.jb.validation.dto.request;

import java.util.List;

public record ValidationResultCreateRequest(
		Double errorRate,
		List<ViolationTextCreateRequest> violations
) {
}
