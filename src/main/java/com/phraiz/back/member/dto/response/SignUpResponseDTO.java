package com.phraiz.back.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 사용자에게 보여줄 정보만 포함
public class SignUpResponseDTO {
    private Long memberId; // 가입된 회원 고유 ID
    private String message; // 가입 결과 메시지

}
