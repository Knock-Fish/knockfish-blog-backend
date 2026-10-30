package com.knockfish.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Data
@Component
public class JwtUtil {
    /** 对应 application.yml 的 private-key（classpath:private_key.pem），@Value 才会解析 classpath: 为 Resource */
    @Value("${jwt.private-key}")
    private Resource privateKeyRes;
    /** 对应 application.yml 的 public-key（classpath:public_key.pem） */
    @Value("${jwt.public-key}")
    private Resource publicKeyRes;
    @Value("${jwt.expiration}")
    private Long expiration;

    /** 启动时构建一次，签名/验签共用 */
    private Algorithm algorithm;

    @PostConstruct
    public void init() throws Exception {
        RSAPrivateKey privateKey = (RSAPrivateKey) loadKey(privateKeyRes, true);
        RSAPublicKey  publicKey  = (RSAPublicKey)  loadKey(publicKeyRes, false);
        // 私钥用于签发，公钥用于验签；Agent 端只持公钥，无法伪造 token
        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
    }

    /** PEM 文本 → Java Key 对象（私钥 PKCS#8 / 公钥 X.509） */
    private java.security.Key loadKey(Resource res, boolean isPrivate) throws Exception {
        String pem = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String b64 = pem.replaceAll("-----BEGIN (.*?)-----", "")
                        .replaceAll("-----END (.*?)-----", "")
                        .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(b64);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return isPrivate ? kf.generatePrivate(new PKCS8EncodedKeySpec(der))
                         : kf.generatePublic(new X509EncodedKeySpec(der));
    }

    private Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * 生成 JWT Token（RS256 签名）
     * @param userId   用户ID
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(Long userId, String username) {
        return JWT.create()
                .withClaim("userId", userId)        // 用户ID声明（Agent 侧取此字段）
                .withClaim("username", username)    // 用户名声明
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .withIssuedAt(new Date())
                .sign(getAlgorithm());
    }

    /**
     * 验证 Token 并获取用户ID
     * @throws JWTVerificationException 如果 Token 无效或过期
     */
    public Long getUserIdFromToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    /**
     * 验证 Token 并返回用户名（subject）
     * 注意：generateToken 未设置 subject，此方法当前返回 null；如需使用请补 withSubject()。
     */
    public String validateTokenAndGetSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
}
