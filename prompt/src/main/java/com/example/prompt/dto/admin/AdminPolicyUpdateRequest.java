package com.example.prompt.dto.admin;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminPolicyUpdateRequest {

    @Min(-1)
    private int dailyChatLimit;

    @Min(0)
    private int imageUploadLimit;

    @Min(0)
    private int fileUploadLimit;

    @Min(0)
    private int fileSizeLimit;

    @Min(0)
    private int tokenLimit;

    @Min(0)
    private int price;
}