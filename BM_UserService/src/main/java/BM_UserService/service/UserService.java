package BM_UserService.service;

import java.util.List;

import BM_UserService.dto.AuthUserResponse;
import BM_UserService.dto.UserRequest;
import BM_UserService.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
    UserResponse getUserById(Long userId);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long userId, UserRequest userRequest);
    AuthUserResponse getUserForAuthentication(String email);
    void deleteUser(Long userId);
    
}