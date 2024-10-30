package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.port.MailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CertificationServiceTest {

    @Test
    public void 이메일과_컨텐가_정상적으로_생성이되며_값을_잘_전달_한다() {
//        given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);

//        when
        certificationService.send("ljy5314@gmail.com",1,"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");

//        then
        assertThat(fakeMailSender.mail).isEqualTo("ljy5314@gmail.com");
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content)
                .isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa");
    }

}