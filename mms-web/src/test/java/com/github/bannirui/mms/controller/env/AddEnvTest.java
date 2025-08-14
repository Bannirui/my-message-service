package com.github.bannirui.mms.controller.env;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bannirui.mms.req.env.AddEnvReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddEnvTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test01() throws Exception {
        AddEnvReq req = new AddEnvReq() {{
            setName("test");
            setSortId(1);
        }};
        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/env/add")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
        ;
    }
}
