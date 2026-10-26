package BM_UserService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import BM_UserService.entity.UserEntity;

public interface UserRepository
        extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByEmail(String email);
}