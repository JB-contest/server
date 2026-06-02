package com.example.jb.user.web;

import com.example.jb.user.dto.request.UserCreateRequest;
import com.example.jb.user.dto.response.UserResponse;
import com.example.jb.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 API")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@Operation(summary = "사용자 목록 조회",
			description = "등록된 전체 사용자 목록을 반환합니다.\n\n"
					+ "**역할(role) 종류**\n"
					+ "- `MARKETING`: 마케팅팀. 문서 업로드 가능\n"
					+ "- `COMPLIANCE`: 준법자문가. 피드백 작성 및 최종 결재 가능")
	public List<UserResponse> users() {
		return userService.users();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "사용자 생성",
			description = "새 사용자를 등록합니다.\n\n"
					+ "- 이메일은 중복 불가\n"
					+ "- `role`: `MARKETING` 또는 `COMPLIANCE`")
	public UserResponse createUser(@RequestBody UserCreateRequest request) {
		return userService.createUser(request);
	}
}
