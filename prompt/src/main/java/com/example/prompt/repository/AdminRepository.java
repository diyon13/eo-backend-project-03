package com.example.prompt.repository;

import com.example.prompt.domain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    /**
     * 관리자 로그인 아이디로 관리자 조회
     */
    Optional<AdminEntity> findByAdminId(String adminId);
}
