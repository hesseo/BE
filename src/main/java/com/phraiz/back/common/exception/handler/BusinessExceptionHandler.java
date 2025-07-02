package com.phraiz.back.common.exception.handler;

import com.phraiz.back.common.exception.ErrorCode;
import com.phraiz.back.common.exception.ErrorResponse;
import com.phraiz.back.common.exception.custom.BusinessLogicException;
import com.phraiz.back.common.util.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessLogicException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        String path = request.getRequestURI();
        LogUtil.logWarn(log, request, errorCode, null);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode, path));
    }
}
