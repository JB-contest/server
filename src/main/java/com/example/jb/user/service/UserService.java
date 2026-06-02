package com.example.jb.user.service;

import com.example.jb.user.domain.User;
import com.example.jb.user.dto.request.UserCreateRequest;
import com.example.jb.user.dto.response.UserResponse;
import com.example.jb.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserResponse> users() {
		return userRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public UserResponse createUser(UserCreateRequest request) {
		if (request == null || !StringUtils.hasText(request.email())
				|| !StringUtils.hasText(request.password())
				|| !StringUtils.hasText(request.name())
				|| request.role() == null) {
			throw new IllegalArgumentException("email, password, name, role을 모두 입력하세요.");
		}
		if (userRepository.findByEmail(request.email()).isPresent()) {
			throw new IllegalStateException("이미 사용 중인 이메일입니다.");
		}
		User user = userRepository.save(User.builder()
				.email(request.email().trim())
				.password(request.password())
				.name(request.name().trim())
				.role(request.role())
				.createdAt(LocalDateTime.now())
				.build());
		return toResponse(user);
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(), user.getCreatedAt());
	}
}
