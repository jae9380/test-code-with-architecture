package com.example.demo.user.service.port;

import com.example.demo.user.infrastructure.entity.UserEntity;
import com.example.demo.user.infrastructure.entity.type.UserStatus;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findById(long id);
    Optional<UserEntity> findByEmailAndStatus(String email, UserStatus userStatus);
    Optional<UserEntity> findByIdAndStatus(long id, UserStatus userStatus);
    UserEntity save(UserEntity userEntity);
}
