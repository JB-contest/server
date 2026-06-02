package com.example.jb.validation.service;

import com.example.jb.validation.domain.ChecklistItem;
import com.example.jb.validation.domain.RiskLevel;
import com.example.jb.validation.dto.internal.ValidationSingleResult;
import com.example.jb.validation.dto.internal.ViolationItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChecklistValidationService {

	private final List<ChecklistItem> checklist;

	public ChecklistValidationService(
			@Value("classpath:checklist.json") Resource resource,
			ObjectMapper objectMapper) {
		try {
			JsonNode root = objectMapper.readTree(resource.getInputStream());
			checklist = objectMapper.convertValue(root.get("checklist"), new TypeReference<>() {});
		} catch (IOException e) {
			throw new IllegalStateException("체크리스트 파일을 불러올 수 없습니다.", e);
		}
	}

	public ValidationSingleResult validate(String content) {
		List<ViolationItem> violations = new ArrayList<>();

		for (ChecklistItem item : checklist) {
			boolean contains = content.contains(item.keyword());
			boolean violated = item.type().equals("REQUIRED") ? !contains : contains;

			if (violated) {
				String violationText = item.type().equals("REQUIRED")
						? item.description()
						: item.keyword();
				violations.add(new ViolationItem(
						violationText,
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
