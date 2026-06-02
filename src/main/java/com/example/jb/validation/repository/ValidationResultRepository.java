package com.example.jb.validation.repository;

import com.example.jb.validation.domain.ValidationResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {
	Optional<ValidationResult> findFirstByDocumentIdOrderByCreatedAtDesc(Long documentId);

	List<ValidationResult> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
}
