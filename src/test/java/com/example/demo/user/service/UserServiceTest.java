package com.example.demo.user.service;

import com.example.demo.commone.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.commone.domain.exception.ResourceNotFoundException;
import com.example.demo.commone.service.port.ClockHolder;
import com.example.demo.commone.service.port.UuidHolder;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {

    private UserServiceImpl userService;

    @BeforeEach
    public void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        UserRepository userRepository = new FakeUserRepository();
        CertificationService certificationService = new CertificationService(fakeMailSender);
        ClockHolder clockHolder = new TestClockHolder(1730026042857L);
        UuidHolder uuidHolder = new TestUuidHolder("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");

        userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Korea")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());

        userRepository.save(User.builder()
                .id(2L)
                .email("ljy531@gmail.com")
                .nickname("bug")
                .address("Korea")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());

        userService = new UserServiceImpl(userRepository, certificationService, uuidHolder, clockHolder);
    }

    @Test
    void getByEmail_ACVIVE_상태인_유저_불러오기() {
//        given
        String email = "ljy5314@gmail.com";
//        when
        User result = userService.getByEmail(email);
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
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void getById_ACVIVE_상태인_유저_불러오기() {
//        given
//        when
        User result = userService.getById(1);
//        then
        assertThat(result.getNickname()).isEqualTo("buckshot");
    }

    @Test
    void getById_PENDING_상태인_유저_불러오면_에러 () {
//        given
//        when
//        then
        assertThatThrownBy(()-> {
            User result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void userCreateDto_유저_생성() {
//        given
        UserCreate dto = UserCreate.builder()
                .email("ljy531@naver.com")
                .address("Andong")
                .nickname("Lee")
                .build();

//        when
        User result = userService.create(dto);

//        then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
    }

    @Test
    void userUpdateDto_유저_생성() {
//        given
        UserUpdate dto = UserUpdate.builder()
                .address("Daegu")
                .nickname("Jae")
                .build();

//        when
         userService.update(1,dto);

//        then
        User result = userService.getById(1);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNickname()).isEqualTo("Jae");
        assertThat(result.getAddress()).isEqualTo("Daegu");
//        TODO: 로그인 시 랜덤으로 생성되는 CertificationCode 확인
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
    }

    @Test
    void 유저가_로그인을_하면_마지막_로그인_시간_갱신() {
//        given

//        when
        userService.login(1);

//        then
        User result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0l);
//        TODO: 마지막 로그인 시간 확인
        assertThat(result.getLastLoginAt()).isEqualTo(1730026042857L);

    }

    @Test
    void PENDING_상태의_유저는_인증을_통해_ACTIVE로_갱신 () {
//        given

//        when
        userService.verifyEmail(2,"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab");

//        then
        User result = userService.getById(2);
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