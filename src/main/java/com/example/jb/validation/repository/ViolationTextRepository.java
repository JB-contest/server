package com.example.jb.validation.repository;

import com.example.jb.validation.domain.ViolationText;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViolationTextRepository extends JpaRepository<ViolationText, Long> {
	List<ViolationText> findByValidationResultIdOrderByIdAsc(Long validationResultId);
}
