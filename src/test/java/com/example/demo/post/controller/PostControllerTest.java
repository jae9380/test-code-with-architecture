package com.example.demo.post.controller;

import com.example.demo.commone.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostJpaRepository;
import com.example.demo.post.infrastructure.entity.PostEntity;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    @Test
    void Post_id를_통하여_Post를_불러오기() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(new TestClockHolder(1730026042850L))
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

        testContainer.postCreateController.createPost(PostCreate.builder()
                .writerId(1L)
                .content("Hello, World")
                .build());

//        when
        ResponseEntity<PostResponse> result = testContainer.postController.getPostById(1);

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody().getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getBody().getContent()).isEqualTo("Hello, World");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1730026042850L);
    }

    @Test
    void Post_id를_통하여_존재하지_않는_Post를_불러오기() throws Exception {
//        given
        TestContainer testContainer = TestContainer.builder()
                .build();

//        when
//        then
        assertThatThrownBy(() -> {
            testContainer.postController.getPostById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void Post_id를_통하여_Post를_변경하기() {
//        given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(new TestClockHolder(1730026042857L))
                .build();

        User user = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .lastLoginAt(100L)
                .build();

        testContainer.userRepository.save(user);

        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("Hello, World")
                .createdAt(1730026042850L)
                .modifiedAt(0L)
                .writer(user)
                .build());

        PostUpdate postUpdate = PostUpdate.builder()
                .content("Hi, There")
                .build();

//        when
        ResponseEntity<PostResponse> result = testContainer.postController.updatePost(1, postUpdate);

//        then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody().getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getBody().getContent()).isEqualTo("Hi, There");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1730026042850L);
        assertThat(result.getBody().getModifiedAt()).isEqualTo(1730026042857L);
    }
}