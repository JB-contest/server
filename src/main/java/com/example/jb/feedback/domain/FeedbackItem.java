package com.example.jb.feedback.domain;

import com.example.jb.validation.domain.ViolationText;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedback_items")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedbackItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "feedback_id", nullable = false)
	private Feedback feedback;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "violation_text_id", nullable = false)
	private ViolationText violationText;

	@Lob
	private String comment;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	public void updateComment(String comment) {
		if (comment == null || comment.isBlank()) {
			throw new IllegalArgumentException("피드백 코멘트를 입력하세요.");
		}
		this.comment = comment.trim();
	}
}
