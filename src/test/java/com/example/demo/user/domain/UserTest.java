package com.example.demo.user.domain;

import com.example.demo.commone.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    public void User는_UserCreate객체로_생성이_가능하다() {
//        given
        UserCreate userCreate = UserCreate.builder()
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .build();

//        when
        User result = User.from(userCreate, new TestUuidHolder("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa"));

//        then
        assertThat(result.getId()).isNull();
        assertThat(result.getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getNickname()).isEqualTo("buckshot");
        assertThat(result.getAddress()).isEqualTo("Andong");
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
    }

    @Test
    public void User는_UserUpdate객체로_수정이_가능하다() {
//        given
        User result = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("Tester1")
                .address("China")
                .build();

//        when
        result = result.update(userUpdate);

//        then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("Tester1");
        assertThat(result.getAddress()).isEqualTo("China");
    }

    @Test
    public void User는_로그인_시_마지막_로그인_시간_갱신() {
//        given
        User result = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//        when
        result =  result.login(new TestClockHolder(1730026042850L));

//        then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getLastLoginAt()).isEqualTo(1730026042850L);
    }

    @Test
    public void User는_유효한_인증_코드_인증을_통해_계정을_활성화() {
//        given
        User result = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//        when
        result =  result.certificate("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");

//        then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void User는_유효하지_않은_인증_코드_인증을_통해_계정을_활성화_실패() {
//        given
        User result = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//        when
//        then
        assertThatThrownBy(()-> result.certificate("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaba"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}