//package com.phraiz.back.common.util;
//
//import com.phraiz.back.member.dto.CustomUserInfoDTO;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.Value;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.User;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.time.ZonedDateTime;
//import java.util.Date;
//
//@Slf4j
//@Component
//// JWT 토큰 생성하고 검증
//// 토큰에 사용자 정보 담아 만들고, 토큰 유효성 검사 및 데이터 추출
//public class JwtUtil {
//    private final Key key;
//    private final Long accessTokenExpTime;
//
//    public JwtUtil(
//            @Value("${jwt.secret-key}") final String secretKey,
//            @Value("${jwt.access-expire}") final long accessTokenExpTime)
//    {
//        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.accessTokenExpTime = accessTokenExpTime;
//    }
//
//    // access token 생성
//    public String createAccessToken(CustomUserInfoDTO member) {
//        return createToken(member, accessTokenExpTime);
//    }
//
//    private String createToken(User member, long accessTokenExpTime) {
//        Claims claims= Jwts.claims();
//        claims.put("member", member);
//
//        ZonedDateTime now = ZonedDateTime.now();
//        ZonedDateTime expTime = now.plusSeconds(accessTokenExpTime);
//
//        return Jwts.builder().setClaims(claims).setIssuedAt(Date.from(now.toInstant())).signWith(key, SignatureAlgorithm.HS256).compact();
//    }
//}
