package com.example.prompt.dto.admin;

import com.example.prompt.dto.common.enums.AdminUserActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AdminDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        @NotBlank
        private String adminId;

        @NotBlank
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
        private String adminId;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class MeResponse {
        private String adminId;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeStatusRequest {

        @NotNull
        private AdminUserActionType action;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePlanRequest {
        private String planName;
    }
}
