package com.shop.security.oauth2.user;

import java.util.Map;

public class KakaoUserInfo extends OAuth2UserInfo {

    private Map<String, Object> kakaoAccount;
    private Map<String, Object> profile;

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
        if(attributes.containsKey("kakao_account")) {
            this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            this.profile = (Map<String,Object>) kakaoAccount.get("profile");
        }
    }

    @Override
    public String getId() {
        return  attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return profile.getOrDefault("nickname","").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.getOrDefault("email","").toString();
    }

    /*@Override
    public abstract String getImageUrl() {
        return profile.getOrDefault("profile_image_url","").toString();
    } */
}
