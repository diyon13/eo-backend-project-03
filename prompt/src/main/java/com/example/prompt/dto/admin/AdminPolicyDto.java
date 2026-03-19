package com.example.prompt.dto.admin;

import com.example.prompt.domain.PlanEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPolicyDto {

    private Long planId;
    private String planName;
    private int tokenLimit;
    private int aiUse;
    private int price;
    private int dailyChatLimit;
    private int imageUploadLimit;
    private int fileUploadLimit;
    private int fileSizeLimit;

    public static AdminPolicyDto from(PlanEntity plan) {
        return AdminPolicyDto.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .tokenLimit(plan.getTokenLimit())
                .aiUse(plan.getAiUse())
                .price(plan.getPrice())
                .dailyChatLimit(plan.getDailyChatLimit())
                .imageUploadLimit(plan.getImageUploadLimit())
                .fileUploadLimit(plan.getFileUploadLimit())
                .fileSizeLimit(plan.getFileSizeLimit())
                .build();
    }
}