package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.UserConvert;
import com.knockfish.dto.user.UserCreateDTO;
import com.knockfish.dto.user.UserQueryDTO;
import com.knockfish.dto.user.UserRoleUpdateDTO;
import com.knockfish.dto.user.UserUpdateDTO;
import com.knockfish.dto.user.UserUpdatePwdDTO;
import com.knockfish.entity.User;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.UserRepository;
import com.knockfish.repository.UserRoleRepository;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.FileReferenceService;
import com.knockfish.service.UserService;
import com.knockfish.vo.user.UserFrontVO;
import com.knockfish.vo.user.UserRoleVO;
import com.knockfish.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final FileReferenceService fileReferenceService;
    private final UserConvert userConvert;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String getPwdByUsername(String username) {
        return userRepository.selectPasswordByUsername(username);
    }

    @Override
    @Cacheable(value = "userCache", key = "'getUserFrontById:' + #userId", unless = "#result == null")
    public UserFrontVO getUserFrontById(Long userId) {
        return userRepository.selectUserFrontById(userId);
    }

    @Override
    public PageInfo<UserVO> getUserList(UserQueryDTO query, Integer pageNum, Integer pageSize){
        try(Page<UserVO> page = PageHelper.startPage(pageNum, pageSize)){
            List<User> userListEntity = userRepository.selectAllUser(query);
            List<UserVO> userListVO = userConvert.userListToUserVOList(userListEntity);

            for (UserVO userVO : userListVO) {
                List<UserRoleVO> roles = getUserRoles(userVO.getUserId());
                userVO.setRoles(roles);
            }

            PageInfo<UserVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(userListVO);
            return pageInfo;
        }
    }

    @Override
    @Cacheable(value = "userCache", key = "'getUserByUsername:' + #username", unless = "#result == null")
    public UserVO getUserByUsername(String username) {
        User userEntity = userRepository.selectByUsername(username);
        return userConvert.userToUserVO(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userCache", allEntries = true)
    public void createUser(UserCreateDTO userCreateDTO) {
        User userEntity = userConvert.createToEntity(userCreateDTO);
        userRepository.insert(userEntity);
        if(userCreateDTO.getFileId() != null) {
            fileReferenceService.updateReferenceId(
                userCreateDTO.getFileId(),
                userEntity.getUserId()
            );
        }
        if (userCreateDTO.getRoleIds() != null && !userCreateDTO.getRoleIds().isEmpty()) {
            userRoleRepository.batchInsert(userEntity.getUserId(), userCreateDTO.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userCache", allEntries = true)
    public void updateUserPwd(UserUpdatePwdDTO userUpdatePwdDTO) {
        Long userId = userUpdatePwdDTO.getUserId();
        log.debug("User ID from DTO: {}", userId);

        if (userId == null) {
            throw new CustomException(401, "无法获取用户信息");
        }

        String currentEncodedPwd = userRepository.selectPasswordById(userId);
        if (currentEncodedPwd == null) {
            throw new CustomException(401, "账号不存在");
        }

        if (!passwordEncoder.matches(userUpdatePwdDTO.getOldPassword(), currentEncodedPwd)) {
            throw new CustomException(401, "原密码错误");
        }

        if (passwordEncoder.matches(userUpdatePwdDTO.getPassword(), currentEncodedPwd)) {
            throw new CustomException(400, "新密码不能与旧密码相同");
        }

        String encodedNewPassword = passwordEncoder.encode(userUpdatePwdDTO.getPassword());
        userRepository.updatePassword(userId, encodedNewPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userCache", allEntries = true)
    public UserVO updateUser(UserUpdateDTO userUpdateDTO) {
        User userEntity = userConvert.updateToEntity(userUpdateDTO);
        userRepository.updateById(userEntity);
        if(userUpdateDTO.getFileId() != null) {
            fileReferenceService.updateReferenceId(
                userUpdateDTO.getFileId(),
                userEntity.getUserId()
            );
        }
        return userConvert.userToUserVO(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "userCache", allEntries = true)
    public void updateUserRoles(UserRoleUpdateDTO userRoleUpdateDTO) {
        Long userId = userRoleUpdateDTO.getUserId();
        List<Long> roleIds = userRoleUpdateDTO.getRoleIds();

        userRoleRepository.deleteByUserId(userId);

        if (roleIds != null && !roleIds.isEmpty()) {
            userRoleRepository.batchInsert(userId, roleIds);
        }
    }

    @Override
    @Cacheable(value = "userCache", key = "'getUserRoles:' + #userId")
    public List<UserRoleVO> getUserRoles(Long userId) {
        List<UserRoleVO> roles = userRepository.selectUserRoleVOByUserId(userId);
        return roles != null ? roles : new ArrayList<>();
    }
}