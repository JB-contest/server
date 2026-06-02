package com.example.jb.document.service;

import com.example.jb.document.domain.Document;
import com.example.jb.document.domain.DocumentStatus;
import com.example.jb.document.dto.request.DocumentCreateRequest;
import com.example.jb.document.dto.request.DocumentRevisionRequest;
import com.example.jb.document.dto.request.DocumentStatusUpdateRequest;
import com.example.jb.document.dto.response.DocumentResponse;
import com.example.jb.document.repository.DocumentRepository;
import com.example.jb.user.domain.Role;
import com.example.jb.user.domain.User;
import com.example.jb.user.repository.UserRepository;
import com.example.jb.validation.domain.ValidationResult;
import com.example.jb.validation.domain.ViolationText;
import com.example.jb.validation.dto.response.ValidationResultResponse;
import com.example.jb.validation.dto.response.ViolationTextResponse;
import com.example.jb.validation.repository.ValidationResultRepository;
import com.example.jb.validation.repository.ViolationTextRepository;
import com.example.jb.validation.service.ValidationOrchestrator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class DocumentService {

	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final ValidationResultRepository validationResultRepository;
	private final ViolationTextRepository violationTextRepository;
	private final ValidationOrchestrator validationOrchestrator;

	public DocumentService(
			DocumentRepository documentRepository,
			UserRepository userRepository,
			ValidationResultRepository validationResultRepository,
			ViolationTextRepository violationTextRepository,
			ValidationOrchestrator validationOrchestrator) {
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
		this.validationResultRepository = validationResultRepository;
		this.violationTextRepository = violationTextRepository;
		this.validationOrchestrator = validationOrchestrator;
	}

	public List<DocumentResponse> documents() {
		return documentRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
	}

	public DocumentResponse document(Long documentId) {
		return toResponse(findDocument(documentId));
	}

	@Transactional
	public DocumentResponse createDocument(DocumentCreateRequest request) {
		if (request == null || request.uploaderId() == null || !StringUtils.hasText(request.name())
				|| request.uploadLocation() == null || request.loanType() == null
				|| !StringUtils.hasText(request.title()) || !StringUtils.hasText(request.content())) {
			throw new IllegalArgumentException("uploaderId, 자료명, 게재 위치, 대출 유형, 제목, 원문을 모두 입력하세요.");
		}
		User uploader = findUser(request.uploaderId());
		if (uploader.getRole() != Role.MARKETING) {
			throw new IllegalArgumentException("문서 업로드 사용자는 MARKETING 역할이어야 합니다.");
		}
		Document saved = documentRepository.save(Document.builder()
				.uploader(uploader)
				.name(request.name().trim())
				.uploadLocation(request.uploadLocation())
				.loanType(request.loanType())
				.title(request.title().trim())
				.content(request.content().trim())
				.status(DocumentStatus.SUBMITTED)
				.createdAt(LocalDateTime.now())
				.build());
		registerValidation(saved.getId());
		return toResponse(saved);
	}

	@Transactional
	public DocumentResponse createRevision(Long parentDocumentId, DocumentRevisionRequest request) {
		Document parent = findDocument(parentDocumentId);
		User uploader = findUser(request.uploaderId());
		if (uploader.getRole() != Role.MARKETING) {
			throw new IllegalArgumentException("문서 업로드 사용자는 MARKETING 역할이어야 합니다.");
		}
		if (!StringUtils.hasText(request.name()) || request.uploadLocation() == null
				|| request.loanType() == null || !StringUtils.hasText(request.title())
				|| !StringUtils.hasText(request.content())) {
			throw new IllegalArgumentException("자료명, 게재 위치, 대출 유형, 제목, 원문을 모두 입력하세요.");
		}
		Document saved = documentRepository.save(Document.builder()
				.uploader(uploader)
				.parent(parent)
				.version(parent.getVersion() + 1)
				.name(request.name().trim())
				.uploadLocation(request.uploadLocation())
				.loanType(request.loanType())
				.title(request.title().trim())
				.content(request.content().trim())
				.status(DocumentStatus.SUBMITTED)
				.createdAt(LocalDateTime.now())
				.build());
		registerValidation(saved.getId());
		return toResponse(saved);
	}

	@Transactional
	public DocumentResponse updateDocumentStatus(Long documentId, DocumentStatusUpdateRequest request) {
		if (request == null || request.status() == null) {
			throw new IllegalArgumentException("변경할 문서 상태를 입력하세요.");
		}
		Document document = findDocument(documentId);
		document.changeStatus(request.status());
		return toResponse(documentRepository.save(document));
	}

	public List<ValidationResultResponse> validationResults(Long documentId) {
		findDocument(documentId);
		return validationResultRepository.findByDocumentIdOrderByCreatedAtDesc(documentId).stream()
				.map(this::toValidationResultResponse)
				.toList();
	}

	private void registerValidation(Long documentId) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				validationOrchestrator.runValidation(documentId);
			}
		});
	}

	private ValidationResultResponse toValidationResultResponse(ValidationResult result) {
		List<ViolationTextResponse> violations = violationTextRepository
				.findByValidationResultIdOrderByIdAsc(result.getId()).stream()
				.map(this::toViolationTextResponse)
				.toList();
		return new ValidationResultResponse(result.getId(), result.getDocument().getId(),
				result.getErrorRate(), result.getCreatedAt(), violations);
	}

	private ViolationTextResponse toViolationTextResponse(ViolationText v) {
		return new ViolationTextResponse(v.getId(), v.getValidationResult().getId(),
				v.getViolationText(), v.getRiskLevel(), v.getViolationTypes(), v.getLawMappings(), v.getCreatedAt());
	}

	private DocumentResponse toResponse(Document document) {
		return new DocumentResponse(
				document.getId(),
				document.getParent() != null ? document.getParent().getId() : null,
				document.getVersion(),
				document.getUploader().getId(),
				document.getUploader().getName(),
				document.getName(),
				document.getUploadLocation(),
				document.getLoanType(),
				document.getTitle(),
				document.getContent(),
				document.getStatus(),
				document.getCreatedAt(),
				document.getUpdatedAt());
	}

	public Document findDocument(Long documentId) {
		return documentRepository.findById(documentId)
				.orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));
	}

	private User findUser(Long userId) {
		if (userId == null) throw new IllegalArgumentException("사용자 ID를 입력하세요.");
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}
}
