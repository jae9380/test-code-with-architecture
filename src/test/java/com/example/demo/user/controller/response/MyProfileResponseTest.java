package com.example.demo.user.controller.response;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MyProfileResponseTest {

    @Test
    public void User로_응답_객체를_생성할_수_있다() {
//         given
        User user = User.builder()
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(50L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//         when
        MyProfileResponse result = MyProfileResponse.from(user);

//         then
        assertThat(result.getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getNickname()).isEqualTo("buckshot");
        assertThat(result.getAddress()).isEqualTo("Andong");
        assertThat(result.getLastLoginAt()).isEqualTo(50L);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

}