package com.example.jb.approval.repository;

import com.example.jb.approval.domain.Approval;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {
	List<Approval> findAllByOrderByApprovedAtDesc();

	Optional<Approval> findFirstByDocumentIdOrderByApprovedAtDesc(Long documentId);

	boolean existsByDocumentId(Long documentId);
}
