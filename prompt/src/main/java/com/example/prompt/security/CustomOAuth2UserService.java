package com.example.prompt.security;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.domain.UserEntity;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    // 유저 로드
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();


        String provider   = userRequest.getClientRegistration().getRegistrationId();
        String providerId = (String) attributes.get("sub");
        String email      = (String) attributes.get("email");
        String name       = (String) attributes.get("name");

        UserEntity user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> registerOAuth2User(provider, providerId, email, name));
        return new CustomOAuth2UserDetails(user, attributes);
    }

    private UserEntity registerOAuth2User(String provider, String providerId, String email, String name) {
        // 동일 이메일 일반 가입자 있으면 OAuth2 연동
        return userRepository.findByEmail(email).orElseGet(() -> {
            PlanEntity normalPlan = planRepository.findByPlanName("NORMAL")
                    .orElseThrow(() -> new IllegalStateException("기본 플랜이 존재하지 않습니다"));

            UserEntity newUser = UserEntity.builder()
                    .userid(provider + "_" + UUID.randomUUID().toString().substring(0, 8))
                    .username(name)
                    .password("OAUTH2_NO_PASSWORD")
                    .email(email)
                    .plan(normalPlan)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            return userRepository.save(newUser);
        });
    }
}
