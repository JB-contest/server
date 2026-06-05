package com.example.jb.validation.service;

import com.example.jb.validation.domain.LegalBasisEntry;
import com.example.jb.validation.domain.RiskLevel;
import com.example.jb.validation.dto.internal.AiPredictRequest;
import com.example.jb.validation.dto.internal.AiPredictResponse;
import com.example.jb.validation.dto.internal.ValidationSingleResult;
import com.example.jb.validation.dto.internal.ViolationItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiValidationService {

	private final RestClient restClient;
	private final Map<String, LegalBasisEntry> legalBasis;

	public AiValidationService(
			@Value("${ai.predict.url}") String aiPredictUrl,
			@Value("classpath:legal-basis.json") Resource legalBasisResource,
			ObjectMapper objectMapper) {
		this.restClient = RestClient.builder()
				.baseUrl(aiPredictUrl)
				.build();
		try {
			this.legalBasis = objectMapper.readValue(
					legalBasisResource.getInputStream(),
					new TypeReference<>() {}
			);
		} catch (IOException e) {
			throw new IllegalStateException("법령근거 파일을 불러올 수 없습니다.", e);
		}
	}

	public ValidationSingleResult validate(String content) {
		AiPredictResponse response = restClient.post()
				.uri("/ai/predict")
				.body(new AiPredictRequest(content))
				.retrieve()
				.body(AiPredictResponse.class);

		if (response == null || response.violations() == null) {
			return new ValidationSingleResult(0.0, List.of());
		}

		List<ViolationItem> violations = response.violations().stream()
				.map(v -> {
					LegalBasisEntry basis = legalBasis.get(v.label());
					return new ViolationItem(
							v.keyword(),
							basis != null ? basis.description() : "",
							RiskLevel.valueOf(v.riskLevel()),
							basis != null ? basis.violationTypes() : List.of(),
							basis != null ? basis.lawMappings() : List.of()
					);
				})
				.toList();

		return new ValidationSingleResult(response.errorRate(), violations);
	}
}
