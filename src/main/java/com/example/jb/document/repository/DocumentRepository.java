package com.example.jb.document.repository;

import com.example.jb.document.domain.Document;
import com.example.jb.document.domain.DocumentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
	List<Document> findAllByOrderByCreatedAtDesc();

	long countByStatus(DocumentStatus status);

	Optional<Document> findFirstByStatusOrderByUpdatedAtDesc(DocumentStatus status);
}
