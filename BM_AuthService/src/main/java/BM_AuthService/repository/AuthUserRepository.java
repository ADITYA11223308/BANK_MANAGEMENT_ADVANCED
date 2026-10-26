package BM_AuthService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import BM_AuthService.entity.AuthUser;

public interface AuthUserRepository
        extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByEmail(String email);

    boolean existsByEmail(String email);
}