package com.example.jb.approval.service;

import com.example.jb.approval.domain.Approval;
import com.example.jb.approval.dto.request.ApprovalCreateRequest;
import com.example.jb.approval.dto.response.ApprovalResponse;
import com.example.jb.approval.repository.ApprovalRepository;
import com.example.jb.document.domain.Document;
import com.example.jb.document.repository.DocumentRepository;
import com.example.jb.user.domain.Role;
import com.example.jb.user.domain.User;
import com.example.jb.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class ApprovalService {

	private final ApprovalRepository approvalRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;

	public ApprovalService(ApprovalRepository approvalRepository,
			DocumentRepository documentRepository,
			UserRepository userRepository) {
		this.approvalRepository = approvalRepository;
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
	}

	public List<ApprovalResponse> approvals() {
		return approvalRepository.findAllByOrderByApprovedAtDesc().stream().map(this::toResponse).toList();
	}

	public ApprovalResponse approval(Long approvalId) {
		return toResponse(approvalRepository.findById(approvalId)
				.orElseThrow(() -> new IllegalArgumentException("승인 정보를 찾을 수 없습니다.")));
	}

	@Transactional
	public ApprovalResponse createApproval(Long documentId, ApprovalCreateRequest request) {
		if (request == null || request.approverId() == null || !StringUtils.hasText(request.reviewNumber())) {
			throw new IllegalArgumentException("approverId와 심의필 번호를 입력하세요.");
		}
		Document document = findDocument(documentId);
		User approver = findUser(request.approverId());
		if (approver.getRole() != Role.COMPLIANCE) {
			throw new IllegalArgumentException("최종 승인자는 COMPLIANCE 역할이어야 합니다.");
		}
		if (approvalRepository.existsByDocumentId(documentId)) {
			throw new IllegalArgumentException("이미 승인된 문서입니다.");
		}
		Approval approval = approvalRepository.save(Approval.builder()
				.document(document)
				.approver(approver)
				.reviewNumber(request.reviewNumber().trim())
				.approvedAt(LocalDateTime.now())
				.build());
		document.approve();
		documentRepository.save(document);
		return toResponse(approval);
	}

	private ApprovalResponse toResponse(Approval approval) {
		return new ApprovalResponse(approval.getId(), approval.getDocument().getId(),
				approval.getApprover().getId(), approval.getApprover().getName(),
				approval.getReviewNumber(), approval.getApprovedAt());
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
}
