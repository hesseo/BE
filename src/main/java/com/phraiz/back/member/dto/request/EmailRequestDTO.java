package com.phraiz.back.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailRequestDTO {
    @Email
    //1)@기호를 포함
    //2)@기호를 기준으로 이메일 주소를 이루는 로컬호스트와 도메인 파트가 존재
    //3)도메인 파트는 최소하나의 점과 그 뒤에 최소한 2개의 알파벳을 가진다를 검증
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;
}
