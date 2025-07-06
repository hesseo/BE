package com.phraiz.back.common.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
// JWT 토큰 생성하고 검증하고 정보 추출용
// 토큰에 사용자 정보 담아 만들고, 토큰 유효성 검사 및 데이터 추출
public class JwtUtil {
    private final Key key;
    private final Long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret-key}") final String secretKey,
            @Value("${jwt.access-expire}") final long accessTokenExpTime)
    {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpTime = accessTokenExpTime;
    }

    // access token 생성
    public String generateToken(String id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpTime);

        // 최종적으로 String 의 JWT 토큰 반환
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(now) // 발급시간
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 파싱해서 subject 꺼내오기
    public String getIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getId();
    }

    // 유효성 체크(만료 여부 등)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (JwtException e) {
            return false;
        }
    }
}
