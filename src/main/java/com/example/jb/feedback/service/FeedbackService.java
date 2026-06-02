package com.example.jb.feedback.service;

import com.example.jb.document.domain.Document;
import com.example.jb.document.repository.DocumentRepository;
import com.example.jb.feedback.domain.Feedback;
import com.example.jb.feedback.domain.FeedbackDecision;
import com.example.jb.feedback.domain.FeedbackItem;
import com.example.jb.feedback.dto.request.FeedbackCreateRequest;
import com.example.jb.feedback.dto.request.FeedbackItemCreateRequest;
import com.example.jb.feedback.dto.request.FeedbackItemUpdateRequest;
import com.example.jb.feedback.dto.response.FeedbackItemResponse;
import com.example.jb.feedback.dto.response.FeedbackResponse;
import com.example.jb.feedback.repository.FeedbackItemRepository;
import com.example.jb.feedback.repository.FeedbackRepository;
import com.example.jb.user.domain.Role;
import com.example.jb.user.domain.User;
import com.example.jb.user.repository.UserRepository;
import com.example.jb.validation.domain.ViolationText;
import com.example.jb.validation.repository.ViolationTextRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class FeedbackService {

	private final FeedbackRepository feedbackRepository;
	private final FeedbackItemRepository feedbackItemRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final ViolationTextRepository violationTextRepository;

	public FeedbackService(
			FeedbackRepository feedbackRepository,
			FeedbackItemRepository feedbackItemRepository,
			DocumentRepository documentRepository,
			UserRepository userRepository,
			ViolationTextRepository violationTextRepository) {
		this.feedbackRepository = feedbackRepository;
		this.feedbackItemRepository = feedbackItemRepository;
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
		this.violationTextRepository = violationTextRepository;
	}

	public List<FeedbackResponse> feedbacks(Long documentId) {
		findDocument(documentId);
		return feedbackRepository.findByDocumentIdOrderByCreatedAtDesc(documentId).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public FeedbackResponse createFeedback(Long documentId, FeedbackCreateRequest request) {
		if (request == null || request.reviewerId() == null || request.decision() == null) {
			throw new IllegalArgumentException("reviewerId와 decision을 입력하세요.");
		}
		Document document = findDocument(documentId);
		User reviewer = findUser(request.reviewerId());
		if (reviewer.getRole() != Role.COMPLIANCE) {
			throw new IllegalArgumentException("피드백 작성자는 COMPLIANCE 역할이어야 합니다.");
		}

		Feedback feedback = feedbackRepository.save(Feedback.builder()
				.document(document)
				.reviewer(reviewer)
				.decision(request.decision())
				.createdAt(LocalDateTime.now())
				.build());

		if (request.items() != null) {
			for (FeedbackItemCreateRequest itemRequest : request.items()) {
				ViolationText violationText = findViolationText(itemRequest.violationTextId());
				feedbackItemRepository.save(FeedbackItem.builder()
						.feedback(feedback)
						.violationText(violationText)
						.comment(trimToNull(itemRequest.comment()))
						.createdAt(LocalDateTime.now())
						.build());
			}
		}

		if (request.decision() == FeedbackDecision.APPROVED) {
			document.startReview();
		} else {
			document.requestRevision();
		}
		documentRepository.save(document);
		return toResponse(feedback);
	}

	@Transactional
	public FeedbackItemResponse updateFeedbackItem(Long itemId, FeedbackItemUpdateRequest request) {
		if (request == null || !StringUtils.hasText(request.comment())) {
			throw new IllegalArgumentException("피드백 코멘트를 입력하세요.");
		}
		FeedbackItem item = feedbackItemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("피드백 항목을 찾을 수 없습니다."));
		item.updateComment(request.comment());
		return toItemResponse(item);
	}

	private FeedbackResponse toResponse(Feedback feedback) {
		List<FeedbackItemResponse> items = feedbackItemRepository.findByFeedbackIdOrderByIdAsc(feedback.getId())
				.stream().map(this::toItemResponse).toList();
		return new FeedbackResponse(feedback.getId(), feedback.getDocument().getId(),
				feedback.getReviewer().getId(), feedback.getReviewer().getName(),
				feedback.getDecision(), feedback.getCreatedAt(), items);
	}

	private FeedbackItemResponse toItemResponse(FeedbackItem item) {
		return new FeedbackItemResponse(item.getId(), item.getFeedback().getId(),
				item.getViolationText().getId(), item.getViolationText().getViolationText(),
				item.getComment(), item.getCreatedAt());
	}

	private Document findDocument(Long documentId) {
		return documentRepository.findById(documentId)
				.orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));
	}

	private User findUser(Long userId) {
		if (userId == null) throw new IllegalArgumentException("사용자 ID를 입력하세요.");
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}

	private ViolationText findViolationText(Long violationTextId) {
		if (violationTextId == null) throw new IllegalArgumentException("위반 문구 ID를 입력하세요.");
		return violationTextRepository.findById(violationTextId)
				.orElseThrow(() -> new IllegalArgumentException("위반 문구를 찾을 수 없습니다."));
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
