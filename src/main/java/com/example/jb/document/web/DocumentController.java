package com.example.jb.document.web;

import com.example.jb.document.dto.request.DocumentCreateRequest;
import com.example.jb.document.dto.request.DocumentRevisionRequest;
import com.example.jb.document.dto.request.DocumentStatusUpdateRequest;
import com.example.jb.document.dto.response.DocumentResponse;
import com.example.jb.document.service.DocumentService;
import com.example.jb.validation.dto.response.ValidationResultResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document", description = "문서 API")
public class DocumentController {

	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@GetMapping
	@Operation(summary = "문서 목록 조회",
			description = "전체 문서 목록을 최신순으로 반환합니다.\n\n"
					+ "**문서 상태(status) 흐름**\n"
					+ "1. `SUBMITTED`: 문서 제출 직후\n"
					+ "2. `AI_VALIDATING`: 체크리스트 + AI 검증 진행 중 (자동 전환)\n"
					+ "3. `IN_REVIEW`: 검증 완료, 준법자문가 검토 대기 (자동 전환)\n"
					+ "4. `REVISION_REQUESTED`: 준법자문가가 수정 요청\n"
					+ "5. `APPROVED`: 최종 결재 완료\n\n"
					+ "**수정본 구분**\n"
					+ "- `parentId`: 원본 문서 id (null이면 최초 문서)\n"
					+ "- `version`: 버전 번호 (최초 제출 시 1, 수정 시 +1)")
	public List<DocumentResponse> documents() {
		return documentService.documents();
	}

	@GetMapping("/{documentId}")
	@Operation(summary = "문서 단건 조회",
			description = "문서 id로 특정 문서를 조회합니다.")
	public DocumentResponse document(@PathVariable Long documentId) {
		return documentService.document(documentId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "문서 생성",
			description = "새 대출 광고 문서를 제출합니다.\n\n"
					+ "- `uploaderId`: MARKETING 역할 사용자만 가능\n"
					+ "- 제출 즉시 체크리스트 + AI 검증이 자동으로 병렬 실행됩니다\n"
					+ "- 응답 시 status는 `SUBMITTED`이며, 검증 완료 후 자동으로 `IN_REVIEW`로 전환됩니다 (완료 여부는 문서 조회 API로 확인)\n\n"
					+ "**uploadLocation 종류**\n"
					+ "- `DETAIL_PAGE`: 상세페이지\n"
					+ "- `CARD_NEWS`: 카드뉴스\n"
					+ "- `FAQ`: FAQ\n"
					+ "- `HOMEPAGE`: 홈페이지\n\n"
					+ "**loanType 종류**\n"
					+ "- `CREDIT_LOAN`: 신용대출\n"
					+ "- `COLLATERAL_LOAN`: 담보대출\n"
					+ "- `JEONSE_LOAN`: 전세자금대출")
	public DocumentResponse createDocument(@RequestBody DocumentCreateRequest request) {
		return documentService.createDocument(request);
	}

	@PostMapping("/{documentId}/revisions")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "문서 수정본 제출",
			description = "준법자문가의 수정 요청(`REVISION_REQUESTED`) 후 수정된 내용으로 새 문서를 제출합니다.\n\n"
					+ "- 원본 문서와 동일한 `uploaderId` 사용 권장\n"
					+ "- `parentId`는 요청한 documentId로 자동 설정\n"
					+ "- `version`은 원본 +1로 자동 설정\n"
					+ "- 제출 즉시 검증이 자동으로 다시 실행됩니다")
	public DocumentResponse createRevision(@PathVariable Long documentId, @RequestBody DocumentRevisionRequest request) {
		return documentService.createRevision(documentId, request);
	}

	@PatchMapping("/{documentId}/status")
	@Hidden
	public DocumentResponse updateDocumentStatus(@PathVariable Long documentId,
			@RequestBody DocumentStatusUpdateRequest request) {
		return documentService.updateDocumentStatus(documentId, request);
	}

	@GetMapping("/{documentId}/validation-results")
	@Operation(summary = "문서 검증 결과 조회",
			description = "문서의 체크리스트 + AI 검증 결과를 반환합니다.\n\n"
					+ "- 문서 상태가 `IN_REVIEW` 이상일 때 결과가 존재합니다\n"
					+ "- `errorRate`: 오류율 (0~100). 체크리스트와 AI 결과의 평균값\n"
					+ "- `violations`: 위반 문구 목록\n\n"
					+ "**riskLevel 종류**\n"
					+ "- `HIGH`: 높음\n"
					+ "- `MEDIUM`: 중간\n"
					+ "- `LOW`: 낮음")
	public List<ValidationResultResponse> validationResults(@PathVariable Long documentId) {
		return documentService.validationResults(documentId);
	}
}
