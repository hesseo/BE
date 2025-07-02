package com.phraiz.back.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SecurityErrorCode implements ErrorCode {

    // 인증 관련
    AUTHENTICATION_FAILED(401, "SEC001", "인증에 실패했습니다.", "SECURITY"),
    INVALID_CREDENTIALS(401, "SEC002", "아이디 또는 비밀번호가 올바르지 않습니다.", "SECURITY"),
    TOKEN_EXPIRED(401, "SEC005", "토큰이 만료되었습니다.", "SECURITY"),
    INVALID_TOKEN(401, "SEC006", "유효하지 않은 토큰입니다.", "SECURITY"),

    // 인가 관련
    ACCESS_DENIED(403, "SEC007", "접근 권한이 없습니다.", "SECURITY"),
    INSUFFICIENT_PERMISSION(403, "SEC008", "요청한 리소스에 접근 권한이 부족합니다.", "SECURITY"),

    // 보안 위협
    CSRF_ATTACK_DETECTED(403, "SEC009", "위조된 요청이 감지되었습니다.", "SECURITY"),
    UNAUTHORIZED_CLIENT(401, "SEC010", "인증되지 않은 클라이언트입니다.", "SECURITY");

    private final int status;
    private final String code;
    private final String message;
    private final String service;

    SecurityErrorCode(int status, String code, String message, String service) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.service = service;
    }
}
