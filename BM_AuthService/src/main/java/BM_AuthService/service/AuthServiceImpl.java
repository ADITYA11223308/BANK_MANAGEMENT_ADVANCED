package BM_AuthService.service;

import org.springframework.stereotype.Service;

import BM_AuthService.client.UserServiceClient;
import BM_AuthService.dto.AuthUserResponse;
import BM_AuthService.dto.LoginRequest;
import BM_AuthService.dto.LoginResponse;
import BM_AuthService.exception.InvalidCredentialsException;
import BM_AuthService.security.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;

    public AuthServiceImpl(UserServiceClient userServiceClient, JwtService jwtService) {

        this.userServiceClient = userServiceClient;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        AuthUserResponse user = userServiceClient.getUserForAuthentication(request.getEmail());

        if (!user.getPassword().equals(request.getPassword())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                        user.getUserId(),
                        user.getEmail(),
                        user.getRole());

        return new LoginResponse(
                "Login successful",
                token,
                user.getUserId(),
                user.getEmail());
    }
}