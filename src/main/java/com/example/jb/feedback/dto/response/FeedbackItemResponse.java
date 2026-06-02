package com.example.jb.feedback.dto.response;

import java.time.LocalDateTime;

public record FeedbackItemResponse(
		Long id,
		Long feedbackId,
		Long violationTextId,
		String violationText,
		String comment,
		LocalDateTime createdAt
) {
}
