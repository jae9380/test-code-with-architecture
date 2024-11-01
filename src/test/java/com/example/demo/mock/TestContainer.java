package com.example.demo.mock;

import com.example.demo.commone.service.port.ClockHolder;
import com.example.demo.commone.service.port.UuidHolder;
import com.example.demo.post.controller.PostController;
import com.example.demo.post.controller.PostCreateController;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final PostService postService;
    public final MailSender mailSender;
    public final CertificationService certificationService;
    public final UserController userController;
    public final UserCreateController userCreateController;
    public final PostCreateController postCreateController;
    public final PostController postController;

    @Builder
    public TestContainer(UuidHolder uuidHolder, ClockHolder clockHolder) {
        this.userRepository = new FakeUserRepository();
        this.postRepository = new FakePostRepository();
        this.mailSender = new FakeMailSender();
        this.certificationService = new CertificationService(this.mailSender);
        this.postService = new PostServiceImpl(
                this.postRepository,
                this.userRepository,
                clockHolder
        );
        UserServiceImpl userService = new UserServiceImpl(
                this.userRepository,
                this.certificationService,
                uuidHolder,
                clockHolder
        );
        this.userController=new UserController(userService);
        this.userCreateController = new UserCreateController(userService);
        this.postCreateController = new PostCreateController(this.postService);
        this.postController = new PostController(this.postService);
    }
}
