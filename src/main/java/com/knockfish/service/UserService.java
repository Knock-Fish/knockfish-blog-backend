package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.user.UserCreateDTO;
import com.knockfish.dto.user.UserQueryDTO;
import com.knockfish.dto.user.UserRoleUpdateDTO;
import com.knockfish.dto.user.UserUpdateDTO;
import com.knockfish.dto.user.UserUpdatePwdDTO;
import com.knockfish.vo.user.UserFrontVO;
import com.knockfish.vo.user.UserRoleVO;
import com.knockfish.vo.user.UserVO;

import java.util.List;

public interface UserService {
    String getPwdByUsername(String username);

    PageInfo<UserVO> getUserList(UserQueryDTO query, Integer pageNum, Integer pageSize);

    UserFrontVO getUserFrontById(Long userId);

    UserVO getUserByUsername(String username);

    void createUser(UserCreateDTO userCreateDTO);

    void updateUserPwd(UserUpdatePwdDTO userUpdatePwdDTO);

    UserVO updateUser(UserUpdateDTO userUpdateDTO);

    void updateUserRoles(UserRoleUpdateDTO userRoleUpdateDTO);

    List<UserRoleVO> getUserRoles(Long userId);

    /**
     * 删除用户（含 user_role 关联清理）
     *
     * @param userId     待删除用户ID
     * @param operatorId 当前登录用户ID，用于拦截"删除自己"
     */
    void deleteUser(Long userId, Long operatorId);
}
