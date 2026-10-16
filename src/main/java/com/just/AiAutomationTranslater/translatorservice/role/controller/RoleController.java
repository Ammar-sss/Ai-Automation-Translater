package com.just.AiAutomationTranslater.translatorservice.role.controller;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.role.dto.RoleDTO;
import com.just.AiAutomationTranslater.translatorservice.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Response<RoleDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {

        Response<RoleDTO> response = roleService.createRole(roleDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<RoleDTO>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {

        Response<RoleDTO> response = roleService.updateRole(id,roleDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteRole(@PathVariable Long id) {

        Response<Void> response = roleService.deleteRole(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<RoleDTO>> getRoleById(@PathVariable Long id) {

        Response<RoleDTO> response = roleService.getRoleById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Response<RoleDTO>> getRoleByName(@PathVariable String name) {

        Response<RoleDTO> response = roleService.getRoleByName(name);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<Response<List<RoleDTO>>> getAllRoles() {

        Response<List<RoleDTO>> response = roleService.getAllRoles();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
