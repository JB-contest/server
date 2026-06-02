package com.example.jb.approval.web;

import com.example.jb.approval.dto.request.ApprovalCreateRequest;
import com.example.jb.approval.dto.response.ApprovalResponse;
import com.example.jb.approval.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Approval", description = "결재 API")
public class ApprovalController {

	private final ApprovalService approvalService;

	public ApprovalController(ApprovalService approvalService) {
		this.approvalService = approvalService;
	}

	@GetMapping("/approvals")
	@Operation(summary = "결재 목록 조회",
			description = "최종 승인된 전체 결재 목록을 최신순으로 반환합니다.")
	public List<ApprovalResponse> approvals() {
		return approvalService.approvals();
	}

	@GetMapping("/approvals/{approvalId}")
	@Operation(summary = "결재 단건 조회",
			description = "결재 id로 특정 결재 정보를 조회합니다.")
	public ApprovalResponse approval(@PathVariable Long approvalId) {
		return approvalService.approval(approvalId);
	}

	@PostMapping("/documents/{documentId}/approvals")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "최종 결재",
			description = "준법자문가가 문서를 최종 승인합니다.\n\n"
					+ "- `approverId`: COMPLIANCE 역할 사용자만 가능\n"
					+ "- 피드백 decision이 `APPROVED`인 문서에 대해 호출해야 합니다\n"
					+ "- `reviewNumber`: 심의필 번호 (예: 2026-JB-0001)\n"
					+ "- 결재 완료 시 문서 상태가 `APPROVED`로 전환되며, 동일 문서에 중복 결재는 불가합니다")
	public ApprovalResponse createApproval(@PathVariable Long documentId,
			@RequestBody ApprovalCreateRequest request) {
		return approvalService.createApproval(documentId, request);
	}
}
