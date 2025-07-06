package com.phraiz.back.member.dto.request;

import com.phraiz.back.member.enums.LoginType;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String id;
    private String pwd;
    private LoginType loginType;
}
