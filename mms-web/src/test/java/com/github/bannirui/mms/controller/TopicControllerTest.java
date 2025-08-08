package com.github.bannirui.mms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bannirui.mms.dto.topic.TopicDTO;
import com.github.bannirui.mms.service.domain.topic.TopicEnvironmentInfoVo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TopicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateTopic() throws Exception {
        TopicDTO req = new TopicDTO() {{
            setName("TEST_TOPIC");
            setEnvironments(new ArrayList<>() {{
                add(new TopicEnvironmentInfoVo() {{
                    setEnvId(1L);
                    setServerId(2L);
                }});
            }});
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/topic/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("操作成功")));
    }
}
