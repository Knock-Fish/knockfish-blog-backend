package com.knockfish.security;

import com.knockfish.entity.Permission;
import com.knockfish.entity.User;
import com.knockfish.repository.PermissionRepository;
import com.knockfish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 用户数据源服务
 * 实现 UserDetailsService 接口，提供用户、角色、权限查询能力
 * 适配：账号密码登录 + JWT令牌解析两种认证场景
 */
@Service                // 标记为Spring业务服务类
@RequiredArgsConstructor // 自动注入final成员变量，替代手动构造方法
@Slf4j                  // 开启日志功能，使用log对象打印日志
public class CustomUserDetailsService implements UserDetailsService {

    // 用户数据访问层
    private final UserRepository userRepository;
    // 权限数据访问层
    private final PermissionRepository permissionRepository;

    /**
     * Spring Security 标准方法
     * 【账号密码登录场景】根据用户名加载用户信息、角色、权限
     * @param username 登录用户名
     * @return 封装后的安全用户对象 CustomUserDetails
     * @throws UsernameNotFoundException 用户名不存在抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 根据用户名查询数据库用户
        User user = userRepository.selectByUsername(username);
        // 用户不存在，打印日志并抛出框架指定异常
        if (user == null) {
            log.warn("登录失败，用户名不存在: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 2. 根据用户ID查询对应权限编码集合
        List<String> permissionCodes = permissionRepository.selectPermissionsByUserId(user.getUserId())
                .stream()
                .map(Permission::getPermissionCode) // 提取权限标识（如 system:user:add）
                .collect(Collectors.toList());

        // 3. 根据用户ID查询对应角色集合
        List<String> roles = userRepository.selectRolesByUserId(user.getUserId());
        // 兜底处理：用户未分配任何角色时，默认赋予基础访客角色
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("ROLE_USER");
        }

        // 打印调试日志，记录当前用户权限、角色数量
        log.debug("用户 [{}] 加载完成，权限数量：{}，角色数量：{}",
                username, permissionCodes.size(), roles.size());

        // 封装为Security专属用户对象并返回
        return new CustomUserDetails(user, permissionCodes, roles);
    }

    /**
     * 自定义方法
     * 【JWT令牌解析场景】根据用户ID加载用户、角色、权限
     * JWT过滤器解析出userId后调用此方法完成认证
     * @param userId 用户主键ID
     * @return 封装后的安全用户对象 CustomUserDetails
     */
    public UserDetails loadUserById(Long userId) {
        // 1. 根据用户ID查询用户信息
        User user = userRepository.selectUserById(userId);
        if (user == null) {
            log.warn("根据用户ID查询失败，用户不存在 userId: {}", userId);
            throw new UsernameNotFoundException("用户信息异常，请重新登录");
        }

        // 2. 查询用户权限编码
        List<String> permissionCodes = permissionRepository.selectPermissionsByUserId(userId)
                .stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());

        // 3. 查询用户角色，无角色则默认分配基础角色
        List<String> roles = userRepository.selectRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("ROLE_USER");
        }

        // 封装返回安全用户对象
        return new CustomUserDetails(user, permissionCodes, roles);
    }
}