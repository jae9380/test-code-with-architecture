package com.example.demo.post.controller;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostCreateControllerTest {

    @Test
    void PostCreatDto_Post_생성() {
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

        PostCreate postCreate = PostCreate.builder()
                .writerId(1l)
                .content("Hello, World")
                .build();

//        when
        ResponseEntity<PostResponse> result = testContainer.postCreateController.createPost(postCreate);

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(result.getBody().getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getBody().getContent()).isEqualTo("Hello, World");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1730026042857L);
    }
}