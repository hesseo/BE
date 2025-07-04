package com.phraiz.back.member.controller;

import com.phraiz.back.member.dto.request.SignUpRequestDTO;
import com.phraiz.back.member.dto.response.SignUpResponseDTO;
import com.phraiz.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        SignUpResponseDTO signUpResponseDTO = memberService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(signUpResponseDTO);
    }


}
