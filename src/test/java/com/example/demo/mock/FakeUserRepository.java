package com.example.demo.mock;

import com.example.demo.commone.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import com.example.demo.user.service.port.UserRepository;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {
    private Long aLong = 0L;
    private final ArrayList<User> data = new ArrayList<>();

    @Override
    public User getById(long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("User",id));
    }

    @Override
    public Optional<User> findById(long id) {
        return data.stream()
                .filter(item -> item.getId().equals(id))
                .findAny();
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus userStatus) {
        return data.stream()
                .filter(item -> item.getEmail().equals(email) &&
                        item.getStatus().equals(userStatus))
                .findAny();
    }

    @Override
    public Optional<User> findByIdAndStatus(long id, UserStatus userStatus) {
        return data.stream()
                .filter(item -> item.getId().equals(id) &&
                    item.getStatus().equals(userStatus))
                .findAny();
    }

    @Override
    public User save(User user) {
        if (user.getId()==null||user.getId()==0) {
            User newUser = User.builder()
                    .id(++aLong)
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .address(user.getAddress())
                    .certificationCode(user.getCertificationCode())
                    .status(user.getStatus())
                    .lastLoginAt(user.getLastLoginAt())
                    .build();
            data.add(newUser);
            return newUser;
        }else {
            data.removeIf(item -> Objects.equals(item.getId(), user.getId()));
            data.add(user);
            return user;
        }
    }
}
