package com.example.jb.user.repository;

import com.example.jb.user.domain.Role;
import com.example.jb.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findFirstByRole(Role role);

	Optional<User> findByEmail(String email);
}
