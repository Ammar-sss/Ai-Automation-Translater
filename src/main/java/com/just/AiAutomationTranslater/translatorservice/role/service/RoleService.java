package com.just.AiAutomationTranslater.translatorservice.role.service;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.role.dto.RoleDTO;

import java.util.List;

public interface RoleService {

    Response<RoleDTO> createRole(RoleDTO roleDTO);
    Response<RoleDTO> updateRole(Long id,RoleDTO roleDTO);
    Response<Void> deleteRole(Long id);
    Response<RoleDTO> getRoleById(Long id);
    Response<RoleDTO> getRoleByName(String name);
    Response<List<RoleDTO>> getAllRoles();
}
