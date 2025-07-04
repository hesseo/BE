package com.phraiz.back.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomUserInfoDTO {
    private Long memberId;
    private String email;
    private String id;
    private String role;

}
