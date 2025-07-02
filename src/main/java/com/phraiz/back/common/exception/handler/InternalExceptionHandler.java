package com.phraiz.back.common.exception.handler;

import com.phraiz.back.common.exception.ErrorCode;
import com.phraiz.back.common.exception.ErrorResponse;
import com.phraiz.back.common.exception.GlobalErrorCode;
import com.phraiz.back.common.exception.custom.InternalServerException;
import com.phraiz.back.common.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class InternalExceptionHandler {

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(InternalServerException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        String path = request.getRequestURI();
        LogUtil.logError(log, request, errorCode, ex);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode, path));
    }

    // 모든 미처리 예외 대응
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex, HttpServletRequest request) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        String path = request.getRequestURI();
        LogUtil.logError(log, request, errorCode, ex);
        return ResponseEntity
                .status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.from(GlobalErrorCode.INTERNAL_SERVER_ERROR, path));
    }
}