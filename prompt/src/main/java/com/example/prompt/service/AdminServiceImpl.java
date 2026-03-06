package com.example.prompt.service;

import com.example.prompt.domain.AdminEntity;
import com.example.prompt.dto.admin.AdminDto;
import com.example.prompt.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    /**
     * 관리자 로그인
     */
    @Override
    public AdminDto.LoginResponse login(AdminDto.LoginRequest request) {

        log.info("관리자 로그인 시도 - adminId = {}", request.getAdminId());

        AdminEntity admin = adminRepository.findByAdminId(request.getAdminId())
                .orElseThrow(() -> {
                    log.warn("관리자 로그인 실패 - 존재하지 않는 adminId = {}", request.getAdminId());
                    return new IllegalArgumentException("관리자 아이디가 존재하지 않습니다.");
                });

        if (!admin.getPassword().equals(request.getPassword())) {
            log.warn("관리자 로그인 실패 - 존재하지 않는 adminId = {}", request.getAdminId());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.info("관리자 로그인 성공 - adminId = {}", admin.getAdminId());

        return new AdminDto.LoginResponse(admin.getAdminId(), admin.getAdminName());
    }

    /**
     * 현재 관리자 정보 조회
     */
    @Override
    public AdminDto.MeResponse getMe(String adminId) {

        log.info("관리자 정보 조회 요청 - adminId = {}", adminId);

        AdminEntity admin = adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> {
                    log.warn("관리자 정보 조회 실패 - 존재하지 않는 adminId = {}", adminId);
                    return  new IllegalArgumentException("관리자 정보를 찾을 수 없습니다.");
                });

        log.info("관리자 정보 조회 성공 - adminId={}", admin.getAdminId());

        return new AdminDto.MeResponse(admin.getAdminId(), admin.getAdminName());
    }
}
