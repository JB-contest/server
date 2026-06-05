package com.example.jb.validation.dto.internal;

import java.util.List;

public record AiPredictResponse(double errorRate, List<AiViolationResult> violations) {
}
