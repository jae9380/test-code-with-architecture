package com.example.demo.user.controller;

import com.example.demo.user.entity.type.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/user-controller-test-data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserJpaRepository userJpaRepository;

    private final ObjectMapper objectMapper=new ObjectMapper();

    @Test
    void 사용자는_특정_유저의_정보를_볼_수_있다() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ljy5314@gmail.com"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.nickname").value("buckshot"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 사용자는_존재하지_않는_유저의_정보를_불러올_때() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/users/3121"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 3121를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증_코드로_계정_상태를_ACTIVE로_갱신() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/users/2/verify")
                .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaab"))
                .andExpect(status().isFound());
        UserEntity entity = userJpaRepository.findById(2l).get();
        assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드로_계정_상태를_ACTIVE로_갱신_실패() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa"))
                .andExpect(status().isForbidden());
        UserEntity entity = userJpaRepository.findById(2l).get();
        assertThat(entity.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void 사용자_자신의_정보를_불러오기() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api//users/me")
                .header("EMAIL","ljy5314@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().json("{"
                        + "\"id\": 1,"
                        + "\"email\": \"ljy5314@gmail.com\","
                        + "\"nickname\": \"buckshot\","
                        + "\"address\": \"Korea\","
                        + "\"status\": \"ACTIVE\""
                        + "}"));
    }

    @Test
    void 사용자_자신의_정보를_수정할_수_있다() throws Exception {
//        given
        UserUpdate dto = UserUpdate.builder()
                .nickname("Dell")
                .address("Space")
                .build();
//        when
//        then
        mockMvc.perform(put("/api//users/me")
                        .header("EMAIL","ljy5314@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto )))
                .andExpect(status().isOk())
                .andExpect(content().json("{"
                        + "\"id\": 1,"
                        + "\"email\": \"ljy5314@gmail.com\","
                        + "\"nickname\": \"Dell\","
                        + "\"address\": \"Space\","
                        + "\"status\": \"ACTIVE\""
                        + "}"));
    }
}