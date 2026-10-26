package BM_UserService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import BM_UserService.dto.AuthUserResponse;
import BM_UserService.dto.UserRequest;
import BM_UserService.dto.UserResponse;
import BM_UserService.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest userRequest) {

        UserResponse user =
                userService.registerUser(userRequest);

        return new ResponseEntity<>(
                user,
                HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @PreAuthorize(
            "hasRole('ADMIN') or #userId.toString() == authentication.name"
    )
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId) {

        UserResponse user =
                userService.getUserById(userId);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/auth/{email}")
    public ResponseEntity<AuthUserResponse>
    getUserForAuthentication(
            @PathVariable String email) {

        return ResponseEntity.ok(
                userService.getUserForAuthentication(email));
    }

    @PutMapping("/{userId}")
    @PreAuthorize(
            "hasRole('ADMIN') or #userId.toString() == authentication.name"
    )
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest userRequest) {

        UserResponse user =
                userService.updateUser(
                        userId,
                        userRequest);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUser(userId);

        return ResponseEntity.ok(
                "User deleted successfully.");
    }
}