package com.knockfish.security;

import com.knockfish.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 自定义 UserDetails 实现类
 * 封装登录用户信息、角色、权限，供框架认证与鉴权使用
 */
@Data
public class CustomUserDetails implements UserDetails {

    /**
     * 原始用户实体（数据库用户信息）
     */
    private User user;

    /**
     * 用户权限标识集合（细粒度权限，如：blog:add、system:user:edit）
     */
    private List<String> permissions;

    /**
     * 用户角色集合（如：admin、editor、visitor）
     */
    private List<String> roles;

    /**
     * 全参构造方法
     * @param user 数据库用户实体
     * @param permissions 权限列表
     * @param roles 角色列表
     */
    public CustomUserDetails(User user, List<String> permissions, List<String> roles) {
        this.user = user;
        this.permissions = permissions;
        this.roles = roles;
    }

    /**
     * 获取用户所有权限（角色 + 功能权限）
     * Spring Security 鉴权核心方法，框架会从此处读取角色和权限
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 1. 封装角色：Spring 角色默认要求前缀 ROLE_
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // 2. 封装细粒度权限：自定义前缀 PERMISSION_，用于接口权限校验
        authorities.addAll(permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission))
                .toList());

        return authorities;
    }

    /**
     * 获取用户密码（Security 认证时比对密码）
     * @return 加密后的密码
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取登录用户名（账号）
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 账户是否未过期
     * true = 账户正常，false = 账户已过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     * true = 账户未锁定，false = 账户被锁定（如多次输错密码）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证（密码）是否未过期
     * true = 密码有效，false = 密码已过期需修改
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     * true = 正常可用，false = 账号被禁用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 拓展方法：获取用户ID
     * 业务接口中可从 Security 上下文直接获取登录人ID
     * @return 用户主键ID
     */
    public Long getUserId() {
        return user.getUserId();
    }
}