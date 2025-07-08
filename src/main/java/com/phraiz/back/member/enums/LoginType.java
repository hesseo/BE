package com.phraiz.back.member.enums;

public enum LoginType {
    LOCAL,
    // OAuth
    NAVER, KAKAO, GOOGLE;

    public static LoginType from(String registrationId) {
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId is null");
        }

        switch (registrationId.toLowerCase()) {
            case "google":
                return GOOGLE;
            case "naver":
                return NAVER;
            case "kakao":
                return KAKAO;
            case "local":
                return LOCAL;
            default:
                throw new IllegalArgumentException("Unknown registrationId: " + registrationId);
        }
    }


}
