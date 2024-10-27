package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/user-service-test-data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})

class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void getByEmail_ACVIVE_상태인_유저_불러오기() {
//        given
        String email = "ljy5314@gmail.com";
//        when
        UserEntity result = userService.getByEmail(email);
//        then
        assertThat(result.getNickname()).isEqualTo("buckshot");
    }

    @Test
    void getByEmail_PENDING_상태인_유저_불러오면_에러 () {
//        given
        String email = "ljy531@gmail.com";
//        when
//        then
        assertThatThrownBy(()-> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void getById_ACVIVE_상태인_유저_불러오기() {
//        given
//        when
        UserEntity result = userService.getById(1);
//        then
        assertThat(result.getNickname()).isEqualTo("buckshot");
    }

    @Test
    void getById_PENDING_상태인_유저_불러오면_에러 () {
//        given
//        when
//        then
        assertThatThrownBy(()-> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void userCreateDto_유저_생성() {
//        given
        UserCreateDto dto = UserCreateDto.builder()
                .email("ljy531@naver.com")
                .address("Andong")
                .nickname("Lee")
                .build();

        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

//        when
        UserEntity result = userService.create(dto);

//        then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo();
    }

    @Test
    void userUpdateDto_유저_생성() {
//        given
        UserUpdateDto dto = UserUpdateDto.builder()
                .address("Daegu")
                .nickname("Jae")
                .build();

//        when
         userService.update(1,dto);

//        then
        UserEntity result = userService.getById(1);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNickname()).isEqualTo("Jae");
        assertThat(result.getAddress()).isEqualTo("Daegu");
//        TODO: 로그인 시 랜덤으로 생성되는 CertificationCode 확인
//        assertThat(result.getCertificationCode()).isEqualTo();
    }

    @Test
    void 유저가_로그인을_하면_마지막_로그인_시간_갱신() {
//        given

//        when
        userService.login(1);

//        then
        UserEntity result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0l);
//        TODO: 마지막 로그인 시간 확인
    }

    @Test
    void PENDING_상태의_유저는_인증을_통해_ACTIVE로_갱신 () {
//        given

//        when
        userService.verifyEmail(2,"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab");

//        then
        UserEntity result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_유저_인증_실패 () {
//        given
//        when
//        then
        assertThatThrownBy(()-> {
            userService.verifyEmail(2,"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}