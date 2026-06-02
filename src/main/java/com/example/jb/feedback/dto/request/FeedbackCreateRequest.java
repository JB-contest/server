package com.example.jb.feedback.dto.request;

import com.example.jb.feedback.domain.FeedbackDecision;
import java.util.List;

public record FeedbackCreateRequest(
		Long reviewerId,
		FeedbackDecision decision,
		List<FeedbackItemCreateRequest> items
) {
}
