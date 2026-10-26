package BM_UserService.dto;

import BM_UserService.entity.UserRole;

public class AuthUserResponse {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private UserRole role;

    public AuthUserResponse() {
    }

    public AuthUserResponse(
            Long userId,
            String name,
            String email,
            String password,
            String phoneNumber,
            UserRole role) {

        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}