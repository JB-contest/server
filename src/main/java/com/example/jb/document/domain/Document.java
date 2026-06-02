package com.example.jb.document.domain;

import com.example.jb.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "documents")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "uploader_id", nullable = false)
	private User uploader;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Document parent;

	@Column(nullable = false)
	private int version;

	@Column(length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private UploadLocation uploadLocation;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private LoanType loanType;

	@Column(length = 50)
	private String title;

	@Lob
	@Column(nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private DocumentStatus status;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public void changeStatus(DocumentStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("문서 상태는 null일 수 없습니다.");
		}
		this.status = status;
	}

	public void startReview() {
		this.status = DocumentStatus.IN_REVIEW;
	}

	public void requestRevision() {
		this.status = DocumentStatus.REVISION_REQUESTED;
	}

	public void approve() {
		this.status = DocumentStatus.APPROVED;
	}

	@PrePersist
	void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = now;
		}
		if (updatedAt == null) {
			updatedAt = createdAt;
		}
		if (status == null) {
			status = DocumentStatus.SUBMITTED;
		}
		if (version == 0) {
			version = 1;
		}
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
