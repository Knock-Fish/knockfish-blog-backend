package com.knockfish.service.impl;

import com.knockfish.convert.AuthConvert;
import com.knockfish.dto.auth.AuthLoginDTO;
import com.knockfish.entity.User;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.UserRepository;
import com.knockfish.service.AuthService;
import com.knockfish.utils.JwtUtil;
import com.knockfish.vo.auth.AuthLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthConvert authConvert;
    private final JwtUtil jwtUtil;

    @Override
    public AuthLoginVO login(AuthLoginDTO authLoginDTO) {
        log.debug("开始验证用户: username={}", authLoginDTO.getUsername());
        User user = userRepository.selectByUsername(authLoginDTO.getUsername());
        if (user == null) {
            log.warn("登录失败: 账号不存在, username={}", authLoginDTO.getUsername());
            throw new CustomException(404, "账号不存在");
        }
        if (!passwordEncoder.matches(authLoginDTO.getPassword(), user.getPassword())) {
            log.warn("登录失败: 密码错误, username={}", authLoginDTO.getUsername());
            throw new CustomException(401, "密码错误");
        }
        log.debug("用户验证通过: username={}, userId={}", user.getUsername(), user.getUserId());
        AuthLoginVO authLoginVO = authConvert.loginToVO(user);
        authLoginVO.setToken(jwtUtil.generateToken(
                user.getUserId(),
                user.getUsername()
        ));
        log.info("用户登录成功: username={}, userId={}", user.getUsername(), user.getUserId());
        return authLoginVO;
    }
}
