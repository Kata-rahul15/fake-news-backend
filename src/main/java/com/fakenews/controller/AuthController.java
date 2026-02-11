package com.fakenews.controller;

import com.fakenews.dto.AuthRequest;
import com.fakenews.dto.AuthResponse;
import com.fakenews.model.User;
import com.fakenews.model.UserStatus;
import com.fakenews.repository.UserRepository;
import com.fakenews.security.JwtUtil;
import com.fakenews.service.AuthService;
import com.fakenews.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    public AuthController(
            AuthService authService,
            JwtUtil jwtUtil,
            AuthenticationConfiguration configuration,
            CustomUserDetailsService userDetailsService
    ) throws Exception {

        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = configuration.getAuthenticationManager();
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/ack-warning")
    public void acknowledgeWarning(@RequestParam Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setWarningAcknowledged(true);

        userRepository.save(user);
        System.out.println(
                "LOGIN → status=" + user.getStatus()
                        + ", acknowledged=" + user.isWarningAcknowledged()
        );

    }


    // -------- REGISTER --------
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(null, "Passwords do not match"));
        }

        try {
            User user = authService.registerUser(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullName()
            );

            var userDetails =
                    userDetailsService.loadUserByUsername(user.getEmail());

            String token = jwtUtil.generateToken(
                    userDetails,
                    user.getFullName(),
                    user.getRole().name()
            );

            UserStatus status =
                    user.getStatus() != null ? user.getStatus() : UserStatus.ACTIVE;

            return ResponseEntity.ok(
                    new AuthResponse(
                            token,
                            user.getRole().name(),
                            status.name()
                    )
            );

        } catch (RuntimeException e) {

            if ("EMAIL_EXISTS".equals(e.getMessage())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new AuthResponse(null, "Email already registered"));
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, "Signup failed"));
        }
    }


    // -------- LOGIN --------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        // ✅ STEP 1: Load user FIRST
        User user = authService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid email or password"));
        }

        // 🚫 BLOCK SUSPENDED USERS FIRST
        UserStatus status =
                user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus();

        if (status == UserStatus.SUSPENDED) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(
                            null,
                            "SUSPENDED"
                    ));
        }

        // ✅ STEP 2: Authenticate credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid email or password"));
        }

        // ⚠️ WARNING LOGIC
        String warningStatus = null;
        if (status == UserStatus.WARNED && !user.isWarningAcknowledged()) {
            warningStatus = "WARNED";
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(
                userDetails,
                user.getFullName(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        user.getRole().name(),
                        warningStatus,
                        user.getId()
                )
        );
    }


}
