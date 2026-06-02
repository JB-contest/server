package com.example.jb.user.dto.response;

import com.example.jb.user.domain.Role;
import java.time.LocalDateTime;

public record UserResponse(
		Long id,
		String email,
		String name,
		Role role,
		LocalDateTime createdAt
) {
}
