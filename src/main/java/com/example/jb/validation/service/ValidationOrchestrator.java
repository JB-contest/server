package com.example.jb.validation.service;

import com.example.jb.document.domain.Document;
import com.example.jb.document.domain.DocumentStatus;
import com.example.jb.document.repository.DocumentRepository;
import com.example.jb.validation.domain.ValidationResult;
import com.example.jb.validation.domain.ViolationText;
import com.example.jb.validation.dto.internal.ValidationSingleResult;
import com.example.jb.validation.dto.internal.ViolationItem;
import com.example.jb.validation.repository.ValidationResultRepository;
import com.example.jb.validation.repository.ViolationTextRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ValidationOrchestrator {

	private static final Logger log = LoggerFactory.getLogger(ValidationOrchestrator.class);

	private final DocumentRepository documentRepository;
	private final ValidationResultRepository validationResultRepository;
	private final ViolationTextRepository violationTextRepository;
	private final ChecklistValidationService checklistValidationService;
	private final AiValidationService aiValidationService;

	public ValidationOrchestrator(
			DocumentRepository documentRepository,
			ValidationResultRepository validationResultRepository,
			ViolationTextRepository violationTextRepository,
			ChecklistValidationService checklistValidationService,
			AiValidationService aiValidationService) {
		this.documentRepository = documentRepository;
		this.validationResultRepository = validationResultRepository;
		this.violationTextRepository = violationTextRepository;
		this.checklistValidationService = checklistValidationService;
		this.aiValidationService = aiValidationService;
	}

	@Async
	public void runValidation(Long documentId) {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

		document.changeStatus(DocumentStatus.AI_VALIDATING);
		documentRepository.save(document);

		String content = document.getContent();

		try {
			CompletableFuture<ValidationSingleResult> checklistFuture =
					CompletableFuture.supplyAsync(() -> checklistValidationService.validate(content));
			CompletableFuture<ValidationSingleResult> aiFuture =
					CompletableFuture.supplyAsync(() -> aiValidationService.validate(content));

			ValidationSingleResult checklistResult = checklistFuture.join();
			ValidationSingleResult aiResult = aiFuture.join();

			double avgErrorRate = (checklistResult.errorRate() + aiResult.errorRate()) / 2.0;

			ValidationResult savedResult = validationResultRepository.save(
					ValidationResult.builder()
							.document(document)
							.errorRate(avgErrorRate)
							.createdAt(LocalDateTime.now())
							.build()
			);

			Stream.concat(checklistResult.violations().stream(), aiResult.violations().stream())
					.forEach(v -> violationTextRepository.save(
							ViolationText.builder()
									.validationResult(savedResult)
									.violationText(v.violationText())
									.riskLevel(v.riskLevel())
									.violationTypes(toJsonArray(v.violationTypes()))
									.lawMappings(toJsonArray(v.lawMappings()))
									.createdAt(LocalDateTime.now())
									.build()
					));

			document.startReview();
			documentRepository.save(document);

		} catch (Exception e) {
			log.error("문서 {} 검증 중 오류 발생", documentId, e);
			document.changeStatus(DocumentStatus.SUBMITTED);
			documentRepository.save(document);
		}
	}

	private String toJsonArray(List<String> items) {
		if (items == null || items.isEmpty()) return null;
		return items.stream()
				.map(s -> "\"" + s + "\"")
				.collect(Collectors.joining(", ", "[", "]"));
	}
}
