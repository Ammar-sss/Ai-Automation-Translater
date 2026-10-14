package com.just.AiAutomationTranslater.translatorservice.auth_user.controller;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.UserService;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Response<UserDTO>> getUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteUserByID(@PathVariable Long id){
        return ResponseEntity.ok(userService.deleteUserByID(id));
    }

    @DeleteMapping
    public ResponseEntity<Response<Void>> deleteUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(userService.deleteUserByEmail(email));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Response<Void>> deActivateUserByID(@PathVariable Long id){
        return ResponseEntity.ok(userService.deActivateUserByID(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Response<Void>> activateUserByID(@PathVariable Long id){
        return ResponseEntity.ok(userService.activateUserByID(id));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Response<Void>> deActivateUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(userService.deActivateUserByEmail(email));
    }

}
