package com.fakenews.service;

import com.fakenews.model.Role;
import com.fakenews.model.User;
import com.fakenews.model.UserStatus;
import com.fakenews.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository repo, PasswordEncoder encoder) {
        this.userRepository = repo;
        this.passwordEncoder = encoder;
    }

    // -------- REGISTER USER --------
    public User registerUser(String email, String password, String fullName) {

        // ✅ FIX 1: Normalize email ONCE
        String normalizedEmail = email.trim().toLowerCase();

        // ✅ FIX 2: Explicit existence check (CRITICAL)
        Optional<User> existingUser =
                userRepository.findByEmail(normalizedEmail);

        if (existingUser.isPresent()) {
            throw new RuntimeException("EMAIL_EXISTS");
        }

        // 🔹 EXISTING LOGIC (kept, but corrected)
        User user = new User();

        // ✅ FIX 3: SAVE normalized email (BUG FIX)
        user.setEmail(normalizedEmail);

        user.setPasswordHash(
                passwordEncoder.encode(password)
        );

        user.setFullName(fullName);

        // 🔹 KEEP DEFAULTS (important for login/security)
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }

        return userRepository.save(user);
    }

    // -------- FIND USER BY EMAIL --------
    public User findByEmail(String email) {

        // ✅ FIX 4: Normalize email during lookup
        String normalizedEmail = email.trim().toLowerCase();

        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
}
