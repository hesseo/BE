package com.phraiz.back.member.service;

import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.dto.request.SignUpRequestDTO;
import com.phraiz.back.member.dto.response.SignUpResponseDTO;
import com.phraiz.back.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        // 중복 검사
        if (memberRepository.existsById(signUpRequestDTO.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }
        if (memberRepository.existsByEmail(signUpRequestDTO.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
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
        return new SignUpResponseDTO(member.getMemberId(),"회원가입 성공");
    }
}
