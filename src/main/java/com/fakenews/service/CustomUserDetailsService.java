package com.fakenews.service;

import org.springframework.context.annotation.Primary;
import com.fakenews.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private String calculateBadge(int karmaPoints) {
        if (karmaPoints >= 300) return "Guardian";
        if (karmaPoints >= 150) return "Trusted";
        if (karmaPoints >= 50) return "Contributor";
        return "Newbie";
    }


    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }
}
