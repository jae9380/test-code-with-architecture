package com.example.demo.user.infrastructure;

import com.example.demo.user.entity.type.UserStatus;
import com.example.demo.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(showSql = true)
@Sql("/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;
//    @Test
//    void UserRepository_정상적으로_연결() {
////        given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("ljy5314@gmail.com");
//        userEntity.setAddress("Korea");
//        userEntity.setNickname("buckshot");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
//
////        when
//        UserEntity result = userRepository.save(userEntity);
//
////        then
//        assertThat(result.getId()).isNotNull();
//    }

    @Test
    void findByIdAndStatus_유저_데이터_불러오기() {
//        given
//        when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1L, UserStatus.ACTIVE);

//        then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_데이터가_없으면_Optional_empty() {
//        given
//        when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(2l, UserStatus.ACTIVE);

//        then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus_유저_데이터_불러오기() {
//        given
//        when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("ljy5314@gmail.com", UserStatus.ACTIVE);

//        then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_데이터가_없으면_Optional_empty() {
//        given
//        when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("ljy5314@naver.com", UserStatus.ACTIVE);

//        then
        assertThat(result.isEmpty()).isTrue();
    }
}