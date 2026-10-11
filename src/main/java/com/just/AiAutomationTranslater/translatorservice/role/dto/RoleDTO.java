package com.just.AiAutomationTranslater.translatorservice.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDTO {

    private Long id;

    @NotBlank(message = "Role name must not be empty")
    private String roleName;
}
