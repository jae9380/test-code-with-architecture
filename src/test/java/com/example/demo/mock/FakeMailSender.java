package com.example.demo.mock;

import com.example.demo.user.service.port.MailSender;

public class FakeMailSender implements MailSender {

    public String mail;
    public String title;
    public String content;
    @Override
    public void send(String mail, String title, String content) {
        this.mail=mail;
        this.title=title;
        this.content=content;
    }
}
