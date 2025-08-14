package com.github.bannirui.mms.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private List<String> roles;
    private String introduction;
    private String avatar;
    private String name;
}
