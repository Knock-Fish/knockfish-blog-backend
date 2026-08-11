package com.knockfish.service;

import com.knockfish.dto.auth.AuthLoginDTO;
import com.knockfish.vo.auth.AuthLoginVO;

public interface AuthService {
    AuthLoginVO login(AuthLoginDTO authLoginDTO);
}
