package com.knockfish.repository;

import com.knockfish.dto.user.UserQueryDTO;
import com.knockfish.entity.User;
import com.knockfish.vo.user.UserFrontVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRepository {
    Long insert(User user);

    String selectPasswordByUsername(@Param("username") String username);

    String selectPasswordById(@Param("userId") Long userId);

    UserFrontVO selectUserFrontById(@Param("userId") Long userId);

    User selectUserById(@Param("userId") Long userId);

    List<User> selectAllUser(UserQueryDTO query);

    User selectByUsername(@Param("username") String username);

    void updatePassword(
            @Param("userId") Long userId,
            @Param("password") String password
    );

    void updateById(User user);

    List<String> selectRolesByUserId(@Param("userId") Long userId);

    List<com.knockfish.vo.user.UserRoleVO> selectUserRoleVOByUserId(@Param("userId") Long userId);
}