package com.knockfish.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {
    private String secret;
    private Long expiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * 生成 JWT Token
     * @param userId 用户ID
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        return JWT.create()
                .withClaim("userId", userId) // 添加用户ID声明
                .withClaim("username", username) // 添加用户名声明
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .withIssuedAt(new Date())
                .sign(getAlgorithm());
    }

    /**
     * 验证 Token 并获取用户ID
     * @param token JWT Token
     * @return 用户ID
     * @throws JWTVerificationException 如果 Token 无效或过期
     */
    public Long getUserIdFromToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    /**
     * 验证 Token 并返回用户名
     */
    public String validateTokenAndGetSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
}