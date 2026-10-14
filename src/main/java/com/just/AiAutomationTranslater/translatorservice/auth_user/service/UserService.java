package com.just.AiAutomationTranslater.translatorservice.auth_user.service;

import com.just.AiAutomationTranslater.translatorservice.auth_user.dto.UserDTO;
import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.res.Response;

import java.util.List;

public interface UserService {

    User getCurrentUser();
    Response<UserDTO> getProfile();
    Response<List<UserDTO>> getAllUsers();
    Response<UserDTO> getUserById(long id);
    Response<UserDTO> getUserByEmail(String email);
    Response<UserDTO> updateCurrentUser(UserDTO userDTO);
    Response<Void> deleteUserByID(long id);
    Response<Void> deleteUserByEmail(String email);
    Response<Void> deActivateUserByID(long id);
    Response<Void> activateUserByID(long id);
    Response<Void> deActivateUserByEmail(String email);

}
