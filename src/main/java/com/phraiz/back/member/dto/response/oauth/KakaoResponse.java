package com.phraiz.back.member.dto.response.oauth;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final String email,username;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        // email은 kakao_account 내부에 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.email = (kakaoAccount != null) ? (String) kakaoAccount.get("email") : null;
        this.username = kakaoAccount != null ? (String) kakaoAccount.get("username") : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return username;
    }
}
