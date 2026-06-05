package com.example.jb.validation.service;

import com.example.jb.validation.domain.ChecklistItem;
import com.example.jb.validation.domain.LegalBasisEntry;
import com.example.jb.validation.domain.RiskLevel;
import com.example.jb.validation.dto.internal.ValidationSingleResult;
import com.example.jb.validation.dto.internal.ViolationItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChecklistValidationService {

	private record ChecklistRawEntry(int id, String type, List<String> keywords, String label) {}

	private final List<ChecklistItem> checklist;

	public ChecklistValidationService(
			@Value("classpath:checklist.json") Resource checklistResource,
			@Value("classpath:legal-basis.json") Resource legalBasisResource,
			ObjectMapper objectMapper) {
		try {
			Map<String, LegalBasisEntry> legalBasis = objectMapper.readValue(
					legalBasisResource.getInputStream(),
					new TypeReference<>() {}
			);

			JsonNode root = objectMapper.readTree(checklistResource.getInputStream());
			List<ChecklistRawEntry> rawEntries = objectMapper.convertValue(
					root.get("checklist"), new TypeReference<>() {}
			);

			checklist = rawEntries.stream()
					.map(raw -> {
						LegalBasisEntry basis = legalBasis.get(raw.label());
						return new ChecklistItem(
								raw.id(),
								raw.type(),
								basis != null ? basis.description() : "",
								raw.keywords(),
								basis != null ? basis.riskLevel() : "LOW",
								basis != null ? basis.violationTypes() : List.of(),
								basis != null ? basis.lawMappings() : List.of()
						);
					})
					.toList();
		} catch (IOException e) {
			throw new IllegalStateException("체크리스트 파일을 불러올 수 없습니다.", e);
		}
	}

	public ValidationSingleResult validate(String content) {
		List<ViolationItem> violations = new ArrayList<>();

		for (ChecklistItem item : checklist) {
			boolean contains = item.keywords().stream().anyMatch(content::contains);
			boolean violated = item.type().equals("REQUIRED") ? !contains : contains;

			if (violated) {
				String violationText = item.type().equals("REQUIRED")
						? item.violationTypes().isEmpty() ? "" : item.violationTypes().get(0)
						: item.keywords().stream().filter(content::contains).findFirst().orElse(item.keywords().get(0));
				violations.add(new ViolationItem(
						violationText,
						item.description(),
						RiskLevel.valueOf(item.riskLevel()),
						item.violationTypes(),
						item.lawMappings()
				));
			}
		}

		double errorRate = checklist.isEmpty() ? 0.0 : (double) violations.size() / checklist.size() * 100;
		return new ValidationSingleResult(errorRate, violations);
	}
}
