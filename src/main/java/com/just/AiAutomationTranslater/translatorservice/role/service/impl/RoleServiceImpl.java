package com.just.AiAutomationTranslater.translatorservice.role.service.impl;

import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.role.dto.RoleDTO;
import com.just.AiAutomationTranslater.translatorservice.role.entity.Role;
import com.just.AiAutomationTranslater.translatorservice.role.repository.RoleRepository;
import com.just.AiAutomationTranslater.translatorservice.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<RoleDTO> createRole(RoleDTO roleDTO) {

        log.info("Create role: {}", roleDTO.getRoleName());

        if (roleRepository.existsByRoleName(roleDTO.getRoleName())) {
            throw new ConflictException("Role already exists");
        }

        Role newRole = modelMapper.map(roleDTO, Role.class);
        newRole.setId(null);
        newRole = roleRepository.save(newRole);

        RoleDTO newRoleDTO = modelMapper.map(newRole, RoleDTO.class);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role created successfully")
                .data(newRoleDTO)
                .build();
    }

    @Override
    @Transactional
    public Response<RoleDTO> updateRole(Long id, RoleDTO roleDTO) {

        log.info("Update role: {}", roleDTO.getRoleName());

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        roleRepository.findByRoleName(roleDTO.getRoleName())
                .filter(role -> !role.getId().equals(id))
                .ifPresent(role -> {
                    throw new ConflictException("Role name already exists");
                });

        existingRole.setRoleName(roleDTO.getRoleName());
        Role saved = roleRepository.save(existingRole);
        RoleDTO newRoleDTO = modelMapper.map(saved, RoleDTO.class);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(newRoleDTO)
                .build();
    }

    @Override
    @Transactional
    public Response<Void> deleteRole(Long id) {

        log.info("delete by id: {}", id);

        Role currentRole = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role Not Found"));

        roleRepository.delete(currentRole);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully by id")
                .build();
    }

    @Override
    public Response<RoleDTO> getRoleById(Long id) {

        log.info("get role by id: {}", id);

        Role role = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role Not Found"));

        RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role retrieved successfully by id")
                .data(roleDTO)
                .build();
    }

    @Override
    public Response<RoleDTO> getRoleByName(String name) {

        log.info("get role by role name: {}", name);

        Role role = roleRepository.findByRoleName(name).orElseThrow(() -> new NotFoundException("Role Not Found"));

        RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);

        return Response.<RoleDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role retrieved successfully by role name")
                .data(roleDTO)
                .build();
    }

    @Override
    public Response<List<RoleDTO>> getAllRoles() {

        log.info("get all roles");

        List<Role> roles = roleRepository.findAll();

        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();

        return Response.<List<RoleDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roleDTOs)
                .build();
    }
}
