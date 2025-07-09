package com.phraiz.back.member.controller;

import com.phraiz.back.common.security.jwt.JwtUtil;
import com.phraiz.back.common.security.user.CustomUserDetails;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // accessToken 재발급
    // AccessToken은 만료기간이 짧기 때문에, 매번 로그인하는 대신 RefreshToken으로 연장
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        // "Bearer " 제거
        String refreshToken=refreshTokenHeader.replace("Bearer ", "");

       LoginResponseDTO responseDTO=memberService.refreshToken(refreshToken);
       return ResponseEntity.ok(responseDTO);

    }

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
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("이메일 인증 요청이 들어옴");
        System.out.println("이메일 인증이메일: "+emailRequestDTO.getEmail());
        try {
            String authNum = emailService.joinEmail(emailRequestDTO.getEmail());
            response.put("success", true);
            response.put("message", "인증번호가 발송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            response.put("success", false);
            response.put("message", "이메일 발송에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // 1-2. 인증코드 확인
    @PostMapping("/emails/mailAuthCheck")
    public ResponseEntity<Map<String, Object>> authCheck(@RequestBody @Valid EmailCheckDTO emailCheckDTO) {
        boolean check=emailService.checkAuthNum(emailCheckDTO.getEmail(),emailCheckDTO.getAuthNum());

        Map<String, Object> response = new HashMap<>();
        response.put("success", check);
        response.put("message", check ? "이메일 인증 성공" : "이메일 인증 실패");
        return ResponseEntity.ok(response);

    }
    // 1-3. 탈퇴
    // TODO 비밀번호 재입력하면 서버에서 일치확인 후 탈퇴 처리
//    @PostMapping("/withdraw")
//    public ResponseEntity<String> withdraw(@RequestParam("pwd") String pwd, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        memberService.withdraw(customUserDetails.getUsername());
//        return ResponseEntity.ok("탈퇴 완료");
//    }

    /* 2. 로그인 */
    // 2-1. 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Member member = memberService.login(loginRequestDTO);

        // 토큰 생성
        String accessToken = jwtUtil.generateToken(member.getId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId());

        // refresh token redis에 저장
        redisTemplate.opsForValue().set(
                "RT:"+member.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpTime(),TimeUnit.MILLISECONDS
        );


        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(accessToken,refreshToken,member.getMemberId(),member.getId(),member.getEmail(),member.getRole());
        return ResponseEntity.ok(loginResponseDTO);
    }
    // 2-2. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // bearer 제거
        String jwt=token.replace("Bearer ", "");
        // jwt 만료 시간 계산
        long expire=jwtUtil.getAccessTokenExpTime();
        // redis에 저장(키: 토큰, value: "logout", ttl: 만료시간)
        // accessToken 블랙리스트 등록
        redisTemplate.opsForValue().set(jwt,"logout",expire, TimeUnit.MILLISECONDS);
        // refreshToken redis에서 삭제
        redisTemplate.delete("RT:"+customUserDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "logout success");

        return ResponseEntity.ok(response);

    }

    // 2-3. 아이디, 비밀번호 찾기

    /* 3. 회원정보 수정 */
}
