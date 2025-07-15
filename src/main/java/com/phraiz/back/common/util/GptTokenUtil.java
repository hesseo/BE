package com.phraiz.back.common.util;

public class GptTokenUtil {
    private static final double DEFAULT_TOKEN_FACTOR = 1.5;

    /**
     * 한글/영문 혼합 텍스트의 토큰 수를 근사 계산합니다.
     *
     * @param text 입력 텍스트
     * @return 추정 토큰 수
     */
    public static int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int length = text.length();
        return (int) Math.ceil(length * DEFAULT_TOKEN_FACTOR);
    }

    /**
     * 한글/영문 혼합 텍스트의 토큰 수를 커스텀 factor로 계산합니다.
     *
     * @param text   입력 텍스트
     * @param factor 문자당 토큰 비율 (기본 1.5)
     * @return 추정 토큰 수
     */
    public static int estimateTokenCount(String text, double factor) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        int length = text.length();
        return (int) Math.ceil(length * factor);
    }

}
