package com.phraiz.back.common.exception;

public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final String service;
    private final String path;

    public ErrorResponse(int status, String code, String message, String service, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.service = service;
        this.path = path;
    }

    public static ErrorResponse from(ErrorCode errorCode, String path) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getService(),
                path
        );
    }

    // Getter
    public int getStatus() { return status; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getService() { return service; }
    public String getPath() { return path; }
}
