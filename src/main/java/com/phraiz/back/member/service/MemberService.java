package com.phraiz.back.member.service;

import com.phraiz.back.common.exception.custom.BusinessLogicException;
import com.phraiz.back.common.security.jwt.JwtUtil;
import com.phraiz.back.common.util.RedisUtil;
import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.dto.request.LoginRequestDTO;
import com.phraiz.back.member.dto.request.SignUpRequestDTO;
import com.phraiz.back.member.dto.response.LoginResponseDTO;
import com.phraiz.back.member.dto.response.SignUpResponseDTO;
import com.phraiz.back.member.exception.MemberErrorCode;
import com.phraiz.back.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // token 재발급
    public LoginResponseDTO refreshToken(String refreshToken) {
        // refresh token 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessLogicException(MemberErrorCode.INVALID_REFRESH_TOKEN);
        }

        String id=jwtUtil.getSubjectFromToken(refreshToken);

        // redis 에 저장된 refresh 토큰과 일치확인
        String storedRefreshToken=redisTemplate.opsForValue().get("RT:"+id);

        if (storedRefreshToken==null || !storedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("저장된 refresh token과 일치하지 않습니다.");
        }
        // 새로운 access token 발급
        String newAccessToken = jwtUtil.generateToken(id);

        Member member=memberRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return new LoginResponseDTO(newAccessToken,refreshToken,
                member.getMemberId(),
                member.getId(),
                member.getEmail(),
                member.getRole());
    }
    /* 1. 회원가입 */
    // 1-1. 회원가입
    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        // 중복 검사 + 등록된 이메일인지 확인
        if (memberRepository.existsById(signUpRequestDTO.getId())) {
            throw new BusinessLogicException(MemberErrorCode.USERID_EXISTS);
        }
        if (memberRepository.existsByEmail(signUpRequestDTO.getEmail())) {
            throw new BusinessLogicException(MemberErrorCode.DUPLICATE_EMAIL);
        }
        // 비밀번호 암호화
        String encodedPwd=bCryptPasswordEncoder.encode(signUpRequestDTO.getPwd());

        Member member=Member.builder()
                .id(signUpRequestDTO.getId())
                .pwd(encodedPwd)
                .email(signUpRequestDTO.getEmail())
                .loginType(signUpRequestDTO.getLoginType())
                .build();
        memberRepository.save(member);

        // 회원가입 성공 후 Redis에서 인증 완료 상태 삭제
        redisUtil.deleteData("verified:" + signUpRequestDTO.getEmail());

        return new SignUpResponseDTO(member.getMemberId(),"회원가입 성공");
    }
    // 1-2. 이메일 검증 여부 확인
    public void checkEmailVerified(String email) {
        String verified=redisUtil.getData("verified:"+email);
        if (!"true".equals(verified)) {
            throw new BusinessLogicException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    /* 2. 로그인 */
    // 2-1. 로그인
    public Member login(LoginRequestDTO loginRequestDTO) {
        // 아이디로 사용자 조회
        Member member=memberRepository.findById(loginRequestDTO.getId()).orElseThrow(()->new BusinessLogicException(MemberErrorCode.USER_NOT_FOUND));

       if (!bCryptPasswordEncoder.matches(loginRequestDTO.getPwd(),member.getPwd())){
           throw new BusinessLogicException(MemberErrorCode.PASSWORD_MISMATCH);
       }
       return member;
    }

}
