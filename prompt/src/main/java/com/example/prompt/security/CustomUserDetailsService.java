package com.example.prompt.security;

import com.example.prompt.domain.PlanEntity;
import com.example.prompt.domain.UserEntity;
import com.example.prompt.repository.PlanRepository;
import com.example.prompt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userid));

        // 플랜 만료 체크 → 만료됐으면 NORMAL로 다운그레이드
        if (user.getPlanExpiredAt() != null && LocalDateTime.now().isAfter(user.getPlanExpiredAt())) {
            PlanEntity normalPlan = planRepository.findByPlanName("NORMAL")
                    .orElseThrow(() -> new IllegalStateException("기본 플랜이 존재하지 않습니다"));
            user.setPlan(normalPlan);
            user.setPlanExpiredAt(null);
            log.info("플랜 만료 → NORMAL 다운그레이드 - userId: {}", user.getId());
        }

        return new CustomUserDetails(user);
    }
}