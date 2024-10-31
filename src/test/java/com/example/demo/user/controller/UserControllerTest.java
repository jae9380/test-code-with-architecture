package com.example.demo.user.controller;

import com.example.demo.commone.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.commone.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.example.demo.user.infrastructure.entity.UserEntity;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Test
    void 사용자는_특정_유저의_정보를_볼_수_있다() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build());

//        when
        ResponseEntity<UserResponse> result = UserController.builder()
                .userReadService(testContainer.userReadService)
                .build()
                .getUserById(1);

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("ljy5314@gmail.com");
    }

    @Test
    void 사용자는_존재하지_않는_유저의_정보를_불러올_때() throws Exception {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();

//        when
//        then
        assertThatThrownBy(()->{
            UserController.builder()
                    .userReadService(testContainer.userReadService)
                    .build()
                    .getUserById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void 사용자는_인증_코드로_계정_상태를_ACTIVE로_갱신() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build());

//        when
        ResponseEntity<Void> result =
                testContainer.userController.verifyEmail(1,"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(302 ));
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }


    @Test
    void 사용자는_인증_코드로_계정_상태를_ACTIVE로_갱신_실패() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build());

//        when
//        then
        assertThatThrownBy(()->{
            testContainer.userController.verifyEmail(1,"aaaaaaba-aaaa-aaaa-aaaa-aaaaaaaaaaa");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }


    @Test
    void 사용자_자신의_정보를_불러오기() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(()->1730026042857L)
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build());

//        when
        ResponseEntity<MyProfileResponse> result =
                testContainer.userController.getMyInfo("ljy5314@gmail.com");

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getBody().getAddress()).isEqualTo("Andong");
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(1730026042857L);
    }

    @Test
    void 사용자_자신의_정보를_수정할_수_있다() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build());

//        when
        ResponseEntity<MyProfileResponse> result =
                testContainer.userController.updateMyInfo("ljy5314@gmail.com", UserUpdate.builder()
                        .address("World")
                        .nickname("bug")
                        .build());

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getBody().getAddress()).isEqualTo("World");
        assertThat(result.getBody().getNickname()).isEqualTo("bug");
    }

/*
    @Test
    void 사용자는_특정_유저의_정보를_볼_수_있다() throws Exception {
//        given
        UserController userController = UserController.builder()
                .userReadService(new UserReadService() {
                    @Override
                    public User getByEmail(String email) {
                        return null;
                    }
                    @Override
                    public User getById(long id) {
                        return User.builder()
                                .id(id)
                                .email("ljy5314@gmail.com")
                                .nickname("buckshot")
                                .address("Korea")
                                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                                .status(UserStatus.ACTIVE)
                                .build();
                    }
                })
                .build();

//        when
        ResponseEntity<UserResponse> result = userController.getUserById(1);

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("ljy5314@gmail.com");
    }

    @Test
    void 사용자는_존재하지_않는_유저의_정보를_불러올_때() throws Exception {
//        given
        UserController userController = UserController.builder()
                .userReadService(new UserReadService() {
                    @Override
                    public User getByEmail(String email) {
                        return null;
                    }
                    @Override
                    public User getById(long id) {
                        throw new ResourceNotFoundException("Users", id);
                    }
                })
                .build();

//        when
//        then
        assertThatThrownBy(() -> {
            userController.getUserById(1234);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    위 테스트 처럼 stub을 하는 경우는 선호하지 않는 방법이다.
    어떤 하위 클래스에 어떤 메서드가 호출이 되면, 이런 응답을 줘야한다고 하는 순간 구현을 강제하게 되어버린다.

    즉, 책임을 위임하고, 구현은 알아서 해라... 이것과는 거리가 멀어지게 된다.
 */
}