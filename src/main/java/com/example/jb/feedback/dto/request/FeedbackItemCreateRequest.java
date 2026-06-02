package com.example.jb.feedback.dto.request;

public record FeedbackItemCreateRequest(
		Long violationTextId,
		String comment
) {
}
