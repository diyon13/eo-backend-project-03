package com.example.prompt.repository;

import com.example.prompt.domain.UserEntity;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserid(String userid);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUserid(String userid);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByProviderAndProviderId(String provider, String providerId);

    // 플랜
    long countByPlan_PlanName(String planName);

    // 활성 유저 수
    long countByActiveTrue();

    // 잠긴 계정 수
    long countByLockedTrue();

    // 비활성 계정 수
    long countByActiveFalse();

    // 오늘 가입자 수
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 관리자 사용자 검색 + 플랜/상태 필터 + 페이징
    @Query("""
    SELECT u
    FROM UserEntity u
    WHERE (
            :keyword = ''
            OR u.userid LIKE CONCAT('%', :keyword, '%')
            OR u.email LIKE CONCAT('%', :keyword, '%')
          )
      AND (:plan = '' OR u.plan.planName = :plan)
      AND (
            :status = ''
            OR (:status = 'ACTIVE' AND u.active = true AND u.locked = false)
            OR (:status = 'LOCKED' AND u.locked = true)
            OR (:status = 'DELETED' AND u.active = false)
      )
    """)
    Page<UserEntity> searchUsers(
            @Param("keyword") String keyword,
            @Param("plan") String plan,
            @Param("status") String status,
            Pageable pageable
    );

    // 토큰 초기화 스케줄러용: 활성 유저 전체 조회
    List<UserEntity> findByActiveTrue();
}
