package com.github.bannirui.mms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bannirui.mms.req.ApproveTopicReq;
import com.github.bannirui.mms.service.env.EnvDatasourceService;
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
public class ApproveTopicTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test01() throws Exception {
        ApproveTopicReq req = new ApproveTopicReq() {{
            setPartitions(3);
            setReplication(1);
            setEnvServers(new ArrayList<>() {{
                add(new TopicEnvServerInfo() {{
                    setEnvId(2L);
                    setServerId(3L);
                }});
            }});
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/topic/1/approve")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
        ;
    }
}
