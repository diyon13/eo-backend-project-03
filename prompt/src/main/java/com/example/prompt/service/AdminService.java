package com.example.prompt.service;

import com.example.prompt.dto.admin.AdminDto;

public interface AdminService {
    /**
     * 관리자 로그인
     */
    AdminDto.LoginResponse login(AdminDto.LoginRequest request);

    /**
     * 현재 관리자 정보 조회
     */
    AdminDto.MeResponse getMe(String adminId);
}
