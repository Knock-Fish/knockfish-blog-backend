package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String avatar;
    private String description;
    private String githubUrl;
    private String bilibiliUrl;
    private String background;
    private LocalDateTime createTime;
}
