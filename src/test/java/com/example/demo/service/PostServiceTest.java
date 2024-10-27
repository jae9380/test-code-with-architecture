package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/post-service-test-data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void getById를_통하여_Post_불러오기() {
//        given
//        when
        PostEntity result = postService.getById(1l);

//        then
        assertThat(result.getContent()).isEqualTo("Hello, World");
        assertThat(result.getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
    }

    @Test
    void getById를_통하여_존재하지_않는_Post_불러오기() {
//        given
//        when
//        then
        assertThatThrownBy(()->{
            PostEntity result = postService.getById(2l);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void PostCreateDto_Post_생성() {
//        given
        PostCreateDto dto = PostCreateDto.builder()
                .writerId(1)
                .content("Hi, There")
                .build();
//        when
        PostEntity result = postService.create(dto);
//        then
        assertThat(result.getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getContent()).isEqualTo("Hi, There");
    }

    @Test
    void PostCreateDto_Post_변경() {
//        given
        PostUpdateDto dto = PostUpdateDto.builder()
                .content("hELLO, wORLD")
                .build();
//        when
        postService.update(1,dto);
//        then
        PostEntity result = postService.getById(1l);
        assertThat(result.getContent()).isEqualTo("hELLO, wORLD");
        assertThat(result.getModifiedAt()).isGreaterThan(1730026042857l);
    }
}