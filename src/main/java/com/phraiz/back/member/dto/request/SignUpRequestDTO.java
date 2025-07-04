package com.phraiz.back.member.dto.request;

import com.phraiz.back.member.domain.Member;
import com.phraiz.back.member.enums.LoginType;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String id;
    private String pwd;
    private String email;
    private LoginType loginType;
    private Long planId;

    public Member toEntity() {
        return Member.builder()
                .id(this.id)
                .pwd(this.pwd)  // TODO 비밀번호는 암호화 처리한 후 넣기
                .email(this.email)
                .loginType(this.loginType)
                .planId(this.planId)
                .build();
    }
}
