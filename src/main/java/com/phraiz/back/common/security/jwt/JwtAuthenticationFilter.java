package com.phraiz.back.common.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// HTTP 요청에서 JWT 토큰을 꺼내고, 유효한 경우 인증된 사용자로 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // 검증, 추출
    private final UserDetailsService userDetailsService; // id로 사용자 정보 불러오는 시큐리티 인터페이스

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token=getJwtToken(request);

        if(token!=null && jwtUtil.validateToken(token)){ // 토큰 존재&유효
            String id=jwtUtil.getIdFromToken(token); // 사용자 id 추출
            UserDetails userDetails = userDetailsService.loadUserByUsername(id);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // 시큐리티에서 사용하는 인증 객체 생성
            authentication.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication); // 현재 요청에 대해 "인증된 사용자" 로 등록
        }
        filterChain.doFilter(request, response);
    }

    // jwt 추출
    public String getJwtToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }
}
