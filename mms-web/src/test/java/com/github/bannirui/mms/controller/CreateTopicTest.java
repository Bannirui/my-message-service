package com.github.bannirui.mms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bannirui.mms.req.ApplyTopicReq;
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
public class CreateTopicTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateTopic01() throws Exception {
        ApplyTopicReq req = new ApplyTopicReq() {{
            setUserId(1L);
            setName("test_topic_1");
            setAppId(2L);
            setTps(1000);
            setMsgSz(1000);
            setEnvs(new ArrayList<>() {{
                add(new TopicEnvInfo() {{
                    setEnvId(1L);
                }});
                add(new TopicEnvInfo() {{
                    setEnvId(2L);
                }});
                add(new TopicEnvInfo() {{
                    setEnvId(3L);
                }});
            }});
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/topic/add")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
        ;
    }

    @Test
    public void testCreateTopic02() throws Exception {
        ApplyTopicReq req = new ApplyTopicReq() {{
            setUserId(1L);
            setName("test_topic_2");
            setAppId(2L);
            setTps(1000);
            setMsgSz(1000);
            setEnvs(new ArrayList<>() {{
                add(new TopicEnvInfo() {{
                    setEnvId(2L);
                }});
            }});
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/topic/add")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
        ;
    }
}
