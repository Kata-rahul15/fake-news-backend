package com.fakenews.repository;

import com.fakenews.model.User;
import com.fakenews.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByStatusNot(UserStatus status);

    long count();

}
