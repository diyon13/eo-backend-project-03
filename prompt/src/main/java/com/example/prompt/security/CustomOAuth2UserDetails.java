package com.example.prompt.security;

import com.example.prompt.domain.UserEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class CustomOAuth2UserDetails  extends CustomUserDetails implements OAuth2User {
    public final Map<String, Object> attributes;

    public CustomOAuth2UserDetails(UserEntity user, Map<String, Object> attributes) {
        super(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
