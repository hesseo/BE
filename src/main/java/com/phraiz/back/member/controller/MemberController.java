package com.phraiz.back.member.controller;

import com.phraiz.back.common.security.jwt.JwtUtil;
import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.dto.request.EmailCheckDTO;
import com.phraiz.back.member.dto.request.EmailRequestDTO;
import com.phraiz.back.member.dto.request.LoginRequestDTO;
import com.phraiz.back.member.dto.request.SignUpRequestDTO;
import com.phraiz.back.member.dto.response.LoginResponseDTO;
import com.phraiz.back.member.dto.response.SignUpResponseDTO;
import com.phraiz.back.member.service.EmailService;
import com.phraiz.back.member.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;

    // TODO 토큰 재발급 refresh

    /* 1. 회원가입 */
    // 1-1. 회원 가입(+아이디,이메일 중복 체크)
    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        memberService.checkEmailVerified(signUpRequestDTO.getEmail());
        SignUpResponseDTO signUpResponseDTO = memberService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(signUpResponseDTO);
    }

    // 1-2. 이메일 인증 번호 전송&인증
    @PostMapping("/emails/mailSend")
    public String sendEmail(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        System.out.println("이메일 인증 요청이 들어옴");
        System.out.println("이메일 인증이메일: "+emailRequestDTO.getEmail());
        try {
            return emailService.joinEmail(emailRequestDTO.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    // 1-2. 인증코드 확인
    @PostMapping("/emails/mailAuthCheck")
    public String authCheck(@RequestBody @Valid EmailCheckDTO emailCheckDTO) {
        boolean check=emailService.checkAuthNum(emailCheckDTO.getEmail(),emailCheckDTO.getAuthNum());

        if(check){
            return "success";
        }else{
            return "fail";
        }
    }
    // 1-3. 탈퇴

    /* 2. 로그인 */
    // 2-1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Member member = memberService.login(loginRequestDTO);

        String token = jwtUtil.generateToken(member.getId());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token,member.getMemberId(),member.getId(),member.getEmail(),member.getRole());
        return ResponseEntity.ok(loginResponseDTO);
    }
    // 2-2. 아이디, 비밀번호 찾기

    /* 3. 회원정보 수정 */
}
