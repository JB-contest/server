package com.example.jb.feedback.dto.response;

import com.example.jb.feedback.domain.FeedbackDecision;
import java.time.LocalDateTime;
import java.util.List;

public record FeedbackResponse(
		Long id,
		Long documentId,
		Long reviewerId,
		String reviewerName,
		FeedbackDecision decision,
		LocalDateTime createdAt,
		List<FeedbackItemResponse> items
) {
}
