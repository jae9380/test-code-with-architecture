package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {

    @Test
    public void Post로_응답_객체를_생성할_수_있다() {
//        given
        long createAt = Clock.systemUTC().millis();
        Post post = Post.builder()
                .content("Hello, World")
                .writer(User.builder()
                        .email("ljy5314@gmail.com")
                        .nickname("buckshot")
                        .address("Andong")
                        .build())
                .createdAt(createAt)
                .build();

//        when
        PostResponse result = PostResponse.from(post);

//        then
        assertThat(result.getContent()).isEqualTo("Hello, World");
        assertThat(result.getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getCreatedAt()).isEqualTo(createAt);
    }
}