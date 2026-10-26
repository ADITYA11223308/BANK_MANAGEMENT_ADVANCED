package BM_AuthService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import BM_AuthService.dto.LoginRequest;
import BM_AuthService.dto.LoginResponse;
import BM_AuthService.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {

        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}