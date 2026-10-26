package BM_AuthService.service;

import BM_AuthService.dto.LoginRequest;
import BM_AuthService.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}