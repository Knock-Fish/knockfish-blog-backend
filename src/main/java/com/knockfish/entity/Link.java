package com.knockfish.entity;

import com.knockfish.enums.LinkStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Link {
    private Long linkId;
    private String linkName;
    private String linkUrl;
    private String avatar;
    private String description;
    private LinkStatus status;
    private LocalDateTime createTime;
}
