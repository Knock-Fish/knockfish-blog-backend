package com.knockfish.convert;

import com.knockfish.dto.user.UserCreateDTO;
import com.knockfish.dto.user.UserUpdateDTO;
import com.knockfish.dto.user.UserUpdatePwdDTO;
import com.knockfish.entity.User;
import com.knockfish.vo.user.UserFrontVO;
import com.knockfish.vo.user.UserVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConvert {
    // ==================== DTO -> Entity ====================
    User createToEntity(UserCreateDTO userCreateDTO);
    User updateToEntity(UserUpdateDTO userUpdateDTO);
    User updatePwdToEntity(UserUpdatePwdDTO userUpdatePwdDTO);
    // ==================== Entity -> VO ====================
    UserVO userToUserVO(User user);
    UserFrontVO userToUserFrontVO(User user);
    List<UserVO> userListToUserVOList(List<User> userList);
}
