package com.example.jb.feedback.web;

import com.example.jb.feedback.dto.request.FeedbackCreateRequest;
import com.example.jb.feedback.dto.request.FeedbackItemUpdateRequest;
import com.example.jb.feedback.dto.response.FeedbackItemResponse;
import com.example.jb.feedback.dto.response.FeedbackResponse;
import com.example.jb.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Feedback", description = "피드백 API")
public class FeedbackController {

	private final FeedbackService feedbackService;

	public FeedbackController(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	@GetMapping("/api/documents/{documentId}/feedbacks")
	@Operation(summary = "문서 피드백 목록 조회",
			description = "문서에 달린 피드백 목록을 최신순으로 반환합니다.\n\n"
					+ "각 피드백에는 위반 문구별 코멘트(`items`)가 포함됩니다.")
	public List<FeedbackResponse> feedbacks(@PathVariable Long documentId) {
		return feedbackService.feedbacks(documentId);
	}

	@PostMapping("/api/documents/{documentId}/feedbacks")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "피드백 작성",
			description = "준법자문가가 문서를 검토한 후 피드백을 작성합니다.\n\n"
					+ "- `reviewerId`: COMPLIANCE 역할 사용자만 가능\n"
					+ "- 문서 상태가 `IN_REVIEW`일 때 호출해야 합니다\n\n"
					+ "**decision 종류**\n"
					+ "- `APPROVED`: 승인. 문서 상태 `IN_REVIEW` 유지\n"
					+ "- `REVISION_REQUESTED`: 수정 요청. 문서 상태가 `REVISION_REQUESTED`로 전환\n\n"
					+ "**items**\n"
					+ "- 검증 결과의 위반 문구(`violationTextId`)에 코멘트를 달 수 있습니다\n"
					+ "- 빈 배열로 보내도 됩니다")
	public FeedbackResponse createFeedback(@PathVariable Long documentId,
			@RequestBody FeedbackCreateRequest request) {
		return feedbackService.createFeedback(documentId, request);
	}

	@PutMapping("/api/feedback-items/{itemId}")
	@Operation(summary = "피드백 항목 코멘트 수정",
			description = "작성된 피드백 항목의 코멘트를 수정합니다.")
	public FeedbackItemResponse updateFeedbackItem(@PathVariable Long itemId,
			@RequestBody FeedbackItemUpdateRequest request) {
		return feedbackService.updateFeedbackItem(itemId, request);
	}
}
