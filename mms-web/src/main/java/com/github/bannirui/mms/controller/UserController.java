package com.github.bannirui.mms.controller;

import com.github.bannirui.mms.dto.user.UserDTO;
import com.github.bannirui.mms.req.user.LoginReq;
import com.github.bannirui.mms.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private static final Map<String, String> userName2Token = new HashMap<>() {{
        put("admin", "admin-token");
        put("editor", "editor-token");
    }};
    private static final Map<String, UserDTO> token2UserInfo = new HashMap<>() {{
        put("admin-token", new UserDTO() {{
            setRoles(new ArrayList<String>() {{
                add("admin");
            }});
            setIntroduction("I am a super administrator");
            setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            setName("Super Admin");
            setId(1L);
        }});
        put("editor-token", new UserDTO() {{
            setRoles(new ArrayList<String>() {{
                add("editor");
            }});
            setIntroduction("I am an editor");
            setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            setName("Normal Editor");
            setId(2L);
        }});
    }};

    @PostMapping(value = "/login")
    public Result<String> add(@RequestBody LoginReq req) {
        if (!userName2Token.containsKey(req.getUsername())) {
            return Result.error(60204, "Account and password are incorrect.");
        }
        String token = userName2Token.get(req.getUsername());
        return Result.success(token);
    }

    @GetMapping("/info/{token}")
    public Result<UserDTO> getInfo(@PathVariable String token) {
        UserDTO user = token2UserInfo.get(token);
        if (Objects.isNull(user)) {
            return Result.error(50008, "Login failed, unable to get user details.");
        }
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success(null);
    }
}
