package com.example.jb.compliance.service;

import com.example.jb.document.domain.Document;
import com.example.jb.document.domain.DocumentStatus;
import com.example.jb.document.domain.LoanType;
import com.example.jb.document.domain.UploadLocation;
import com.example.jb.document.repository.DocumentRepository;
import com.example.jb.feedback.domain.Feedback;
import com.example.jb.feedback.domain.FeedbackDecision;
import com.example.jb.feedback.domain.FeedbackItem;
import com.example.jb.feedback.repository.FeedbackItemRepository;
import com.example.jb.feedback.repository.FeedbackRepository;
import com.example.jb.user.domain.Role;
import com.example.jb.user.domain.User;
import com.example.jb.user.repository.UserRepository;
import com.example.jb.validation.domain.RiskLevel;
import com.example.jb.validation.domain.ValidationResult;
import com.example.jb.validation.domain.ViolationText;
import com.example.jb.validation.repository.ValidationResultRepository;
import com.example.jb.validation.repository.ViolationTextRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final ValidationResultRepository validationResultRepository;
	private final ViolationTextRepository violationTextRepository;
	private final FeedbackRepository feedbackRepository;
	private final FeedbackItemRepository feedbackItemRepository;

	public DemoDataSeeder(
			UserRepository userRepository,
			DocumentRepository documentRepository,
			ValidationResultRepository validationResultRepository,
			ViolationTextRepository violationTextRepository,
			FeedbackRepository feedbackRepository,
			FeedbackItemRepository feedbackItemRepository) {
		this.userRepository = userRepository;
		this.documentRepository = documentRepository;
		this.validationResultRepository = validationResultRepository;
		this.violationTextRepository = violationTextRepository;
		this.feedbackRepository = feedbackRepository;
		this.feedbackItemRepository = feedbackItemRepository;
	}

	@Override
	public void run(String... args) {
		if (userRepository.count() > 0) {
			return;
		}

		User marketer = userRepository.save(User.builder()
				.email("marketing@jbfg.com")
				.password("{noop}demo")
				.name("마케팅 담당자")
				.role(Role.MARKETING)
				.createdAt(LocalDateTime.now())
				.build());
		User reviewer = userRepository.save(User.builder()
				.email("compliance@jbfg.com")
				.password("{noop}demo")
				.name("준법 자문가")
				.role(Role.COMPLIANCE)
				.createdAt(LocalDateTime.now())
				.build());

		Document document = documentRepository.save(Document.builder()
				.uploader(marketer)
				.name("봄맞이 신용대출 상세페이지")
				.uploadLocation(UploadLocation.DETAIL_PAGE)
				.loanType(LoanType.CREDIT_LOAN)
				.title("JB 봄맞이 직장인 신용대출")
				.content("직장인이라면 누구나 100% 당일 승인! 업계 최저 금리로 모셔갑니다.")
				.status(DocumentStatus.REVISION_REQUESTED)
				.createdAt(LocalDateTime.now())
				.build());

		ValidationResult result = validationResultRepository.save(ValidationResult.builder()
				.document(document)
				.errorRate(14.0)
				.createdAt(LocalDateTime.now())
				.build());
		ViolationText violation = violationTextRepository.save(ViolationText.builder()
				.validationResult(result)
				.violationText("100% 당일 승인")
				.riskLevel(RiskLevel.HIGH)
				.violationTypes("[\"과장·단정 표현\"]")
				.lawMappings("[\"표시광고법 제3조 2항\"]")
				.createdAt(LocalDateTime.now())
				.build());
		Feedback feedback = feedbackRepository.save(Feedback.builder()
				.document(document)
				.reviewer(reviewer)
				.decision(FeedbackDecision.REVISION_REQUESTED)
				.createdAt(LocalDateTime.now())
				.build());
		feedbackItemRepository.save(FeedbackItem.builder()
				.feedback(feedback)
				.violationText(violation)
				.comment("심사 결과에 따라 당일 심사가 가능하다는 식으로 수정하세요.")
				.createdAt(LocalDateTime.now())
				.build());
	}
}
