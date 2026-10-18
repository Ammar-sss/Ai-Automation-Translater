package com.just.AiAutomationTranslater.translatorservice.auth_user.service.impl;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.repository.UserRepository;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.UserService;
import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.UnauthorizedAccessException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.security.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof AuthUser authUser)) {
            throw new UnauthorizedAccessException("Authentication required");
        }

        return authUser.getUser();
    }

    @Override
    public Response<UserDTO> getProfile() {
        User currentUser = getCurrentUser();

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("users retrieved successfully by id")
                .data(modelMapper.map(currentUser, UserDTO.class))
                .build();
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> userDTOS = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved successfully")
                .data(userDTOS)
                .build();
    }

    @Override
    public Response<UserDTO> getUserById(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("users retrieved successfully by id")
                .data(modelMapper.map(user, UserDTO.class))
                .build();
    }

    @Override
    public Response<UserDTO> getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("users retrieved successfully by id")
                .data(modelMapper.map(user, UserDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response<UserDTO> updateProfile(UserDTO userDTO) {
        User user = getCurrentUser();

        if(!userDTO.getUsername().isBlank()) {
            user.setUsername(userDTO.getUsername());
        }

        userRepository.save(user);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(modelMapper.map(user, UserDTO.class))
                .build();
    }

    @Override
    @Transactional
    public Response<Void> deleteUserByID(long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.delete(user);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User: " + user.getUsername() + " deleted successfully by username")
                .build();
    }

    @Override
    @Transactional
    public Response<Void> deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        userRepository.delete(user);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User: " + user.getEmail() + " deleted successfully by email")
                .build();
    }

    @Override
    @Transactional
    public Response<Void> deActivateUserByID(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEnabled()){
            throw new ConflictException("User already deActivate");
        }

        user.setEnabled(false);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User deActivate successfully by id")
                .build();
    }

    @Override
    public Response<Void> activateUserByID(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isEnabled()){
            throw new ConflictException("User already activate");
        }

        user.setEnabled(true);
        userRepository.save(user);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User activate successfully by id")
                .build();
    }

    @Override
    public Response<Void> deActivateUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEnabled()){
            throw new ConflictException("User already deActivate");
        }

        user.setEnabled(false);
        userRepository.save(user);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User deActivate successfully by email")
                .build();
    }

}
