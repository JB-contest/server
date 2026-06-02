package com.example.jb.user.dto.request;

import com.example.jb.user.domain.Role;

public record UserCreateRequest(
		String email,
		String password,
		String name,
		Role role
) {
}
