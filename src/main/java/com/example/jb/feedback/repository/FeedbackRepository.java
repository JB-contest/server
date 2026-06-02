package com.example.jb.feedback.repository;

import com.example.jb.feedback.domain.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	Optional<Feedback> findFirstByDocumentIdOrderByCreatedAtDesc(Long documentId);

	List<Feedback> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
}
