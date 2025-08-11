package com.github.bannirui.mms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bannirui.mms.req.ApproveTopicReq;
import com.github.bannirui.mms.req.TopicPageReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicQueryTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test01() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/topic/querypage")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
        ;
    }
}
