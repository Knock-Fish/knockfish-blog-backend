package com.knockfish.convert;

import com.knockfish.entity.User;
import com.knockfish.vo.auth.AuthLoginVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthConvert {
    // ==================== Entity -> VO ====================
    AuthLoginVO loginToVO(User user);
}
