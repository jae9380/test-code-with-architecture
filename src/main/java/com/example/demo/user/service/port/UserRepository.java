package com.example.demo.user.service.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;

import java.util.Optional;

public interface UserRepository {
    User getById(long id);
    Optional<User> findById(long id);
    Optional<User> findByEmailAndStatus(String email, UserStatus userStatus);
    Optional<User> findByIdAndStatus(long id, UserStatus userStatus);
    User save(User user);
}
