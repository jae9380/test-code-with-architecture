package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    public void User로_응답_객체를_생성할_수_있다() {
//         given
        User user = User.builder()
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//         when
        UserResponse result = UserResponse.from(user);

//         then
        assertThat(result.getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getNickname()).isEqualTo("buckshot");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);

    }
}