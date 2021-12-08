package com.shop.security.oauth2.user;

import com.shop.constant.AuthProvider;
import com.shop.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.kakao.toString())) {
            return new KakaoUserInfo(attributes);
        } else if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleUserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(registrationId+" 로그인은 아직 지원을 하지 않습니다.");
        }
    }
}
