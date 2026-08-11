package com.knockfish.vo.link;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.knockfish.enums.LinkStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class LinkVO {
    private Long linkId;
    private String linkName;
    private String linkUrl;
    private String avatar;
    private String description;
    private LinkStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
