package com.knockfish.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfig {
    private Dir dir;
    @Data
    public static class Dir {
        private String avatar;
        private String cover;
        private String article;
        private String note;
        private String background;
    }
}
