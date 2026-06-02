package com.example.jb.document.dto.request;

import com.example.jb.document.domain.DocumentStatus;

public record DocumentStatusUpdateRequest(DocumentStatus status) {
}
