package com.knockfish.dto.link;

import com.knockfish.enums.LinkStatus;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LinkCreateDTO {
    private String linkName;
    private String linkUrl;
    private String avatar;
    private String description;
    private LinkStatus status;
}
