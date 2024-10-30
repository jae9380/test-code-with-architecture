package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    public void User는_PostCreate객체로_Post를_생성할_수_있다() {
//        given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1l)
                .content("Hello, World")
                .build();

        User writer = User.builder()
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

//        when
        Post post = Post.from(writer, postCreate);

//        then
        assertThat(post.getContent()).isEqualTo("Hello, World");
        assertThat(post.getCreatedAt()).isGreaterThan(0l);
        assertThat(post.getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    public void User는_PostUpdate객체로_Post를_생성할_수_있다() {
//        given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1l)
                .content("Hello, World")
                .build();

        User writer = User.builder()
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Andong")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .build();

        Post post = Post.from(writer, postCreate);

        PostUpdate postUpdate = PostUpdate.builder()
                .content("Hi, There").build();

//        when
        post = post.update(postUpdate);

//        then
        assertThat(post.getContent()).isEqualTo("Hi, There");
        assertThat(post.getModifiedAt()).isNotNull();

    }
}