package com.phraiz.back.member.dto.response.oauth;

import java.util.Map;

public interface OAuth2Response {
    Map<String, Object> getAttributes();
    String getProvider(); // 제공자: 네이버, 구글, 카카오
    String getProviderId(); // 제공자에서 부여하는 아이디
    String getEmail();
    String getName();
}
