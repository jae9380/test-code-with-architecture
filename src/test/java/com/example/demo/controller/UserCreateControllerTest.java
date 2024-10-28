package com.example.demo.controller;

import com.example.demo.user.dto.UserCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper=new ObjectMapper();
    @Test
    void 새로운_PENDING_상태의_사용자_생성() throws Exception {
//        given
        UserCreateDto dto = UserCreateDto.builder()
                .email("ljy5314@gmail.com")
                .nickname("Dell")
                .address("Space")
                .build();

        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
//        when
//        then
        mockMvc.perform(post("/api/users")
                        .header("EMAIL","ljy5314@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto )))
                .andExpect(status().isCreated())
                .andExpect(content().json("{"
                        + "\"id\": 1,"
                        + "\"email\": \"ljy5314@gmail.com\","
                        + "\"nickname\": \"Dell\","
                        + "\"status\": \"PENDING\""
                        + "}"));
    }
}