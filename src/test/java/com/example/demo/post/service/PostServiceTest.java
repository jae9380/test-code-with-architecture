package com.example.demo.post.service;

import com.example.demo.commone.domain.exception.ResourceNotFoundException;
import com.example.demo.commone.service.port.ClockHolder;
import com.example.demo.mock.*;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.infrastructure.entity.type.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostServiceTest {

    private PostServiceImpl postService;

    @BeforeEach
    public void init() {
        PostRepository postRepository = new FakePostRepository();
        UserRepository userRepository = new FakeUserRepository();
        ClockHolder clockHolder = new TestClockHolder(1730026042857L);

        User user1 = User.builder()
                .id(1L)
                .email("ljy5314@gmail.com")
                .nickname("buckshot")
                .address("Korea")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("ljy531@gmail.com")
                .nickname("bug")
                .address("Korea")
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        postRepository.save(Post.builder()
                .id(1L)
                .content("Hello, World")
                .createdAt(1730026042850L)
                .modifiedAt(1730026042857L)
                .writer(user1)
                .build());

        postService = new PostServiceImpl(postRepository, userRepository, clockHolder);
    }

    @Test
    void getById를_통하여_Post_불러오기() {
//        given
//        when
        Post result = postService.getById(1l);

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
            Post result = postService.getById(2l);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void PostCreateDto_Post_생성() {
//        given
        PostCreate dto = PostCreate.builder()
                .writerId(1)
                .content("Hi, There")
                .build();
//        when
        Post result = postService.create(dto);
//        then
        assertThat(result.getWriter().getEmail()).isEqualTo("ljy5314@gmail.com");
        assertThat(result.getContent()).isEqualTo("Hi, There");
    }

    @Test
    void PostCreateDto_Post_변경() {
//        given
        PostUpdate dto = PostUpdate.builder()
                .content("hELLO, wORLD")
                .build();
//        when
        postService.update(1,dto);
//        then
        Post result = postService.getById(1l);
        assertThat(result.getContent()).isEqualTo("hELLO, wORLD");
        assertThat(result.getModifiedAt()).isEqualTo(1730026042857L);
    }
}