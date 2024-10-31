package com.example.demo.mideum.post.controller;

import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.entity.PostEntity;
import com.example.demo.post.infrastructure.PostJpaRepository;
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
        @Sql(value = "/post-controller-test-data.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/delete-all-data.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostJpaRepository postJpaRepository;
    private final ObjectMapper objectMapper=new ObjectMapper();
    @Test
    void Post_id를_통하여_Post를_불러오기() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Hello, World"))
                .andExpect(jsonPath("$.writer.email").value("ljy5314@gmail.com"))
                .andExpect(jsonPath("$.createdAt").value(1730026042850l))
                .andExpect(jsonPath("$.modifiedAt").value(1730026042857l));
    }

    @Test
    void Post_id를_통하여_존재하지_않는_Post를_불러오기() throws Exception {
//        given
//        when
//        then
        mockMvc.perform(get("/api/posts/51231"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID 51231를 찾을 수 없습니다."));
    }

    @Test
    void Post_id를_통하여_Post를_변경하기() throws Exception {
//        given
        PostUpdate dto = PostUpdate.builder()
                .content("Hi, There")
                .build();
//        when
//        then
        mockMvc.perform(put( "/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Hi, There"))
                .andExpect(jsonPath("$.writer.email").value("ljy5314@gmail.com"));

        PostEntity entity = postJpaRepository.findById(1l).get();
        assertThat(entity.getModifiedAt()).isGreaterThan(1730026042857l);
    }
}