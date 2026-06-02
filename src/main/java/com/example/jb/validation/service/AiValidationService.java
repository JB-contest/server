package com.example.jb.validation.service;

import com.example.jb.validation.dto.internal.ValidationSingleResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiValidationService {

	// TODO: AI팀 구현 예정
	// content를 AI 모델에 전달하고 분석 결과를 ValidationSingleResult로 반환
	// - errorRate: 0.0 ~ 100.0
	// - violations: 위반 문구 목록 (violationText, riskLevel, violationTypes, lawMappings)
	public ValidationSingleResult validate(String content) {
		return new ValidationSingleResult(0.0, List.of());
	}
}
