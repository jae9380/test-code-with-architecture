package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/user-service-test-data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})

class UserServiceTest {

    @Autowired
    private UserService userService;

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
    
}