package com.example.jb.feedback.repository;

import com.example.jb.feedback.domain.FeedbackItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackItemRepository extends JpaRepository<FeedbackItem, Long> {
	List<FeedbackItem> findByFeedbackIdOrderByIdAsc(Long feedbackId);

	List<FeedbackItem> findByFeedback_Document_IdOrderByIdAsc(Long documentId);
}
