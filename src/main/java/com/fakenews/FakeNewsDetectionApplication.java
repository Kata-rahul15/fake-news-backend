package com.fakenews;

import com.fakenews.model.Role;
import com.fakenews.model.User;
import com.fakenews.model.UserStatus;
import com.fakenews.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FakeNewsDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeNewsDetectionApplication.class, args);
    }

}




