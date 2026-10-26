package BM_UserService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import BM_UserService.dto.AuthUserResponse;
import BM_UserService.dto.UserRequest;
import BM_UserService.dto.UserResponse;

import BM_UserService.entity.UserEntity;
import BM_UserService.entity.UserRole;

import BM_UserService.exception.UserNotFoundException;
import BM_UserService.repository.UserRepository;

import BM_UserService.kafka.UserProducer;
import BM_UserService.kafka.UserRegisteredEvents;
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserProducer userProducer;
	
	public UserServiceImpl(
	        UserRepository userRepository,
	        UserProducer userProducer) {

	    this.userRepository = userRepository;
	    this.userProducer = userProducer;
	}

	@Override
	public UserResponse registerUser(UserRequest userRequest) {

	    if (userRepository.existsByEmail(userRequest.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
	        throw new RuntimeException("Phone number already exists");
	    }

	    UserEntity user = new UserEntity();

	    user.setName(userRequest.getName());
	    user.setEmail(userRequest.getEmail());
	    user.setPassword(userRequest.getPassword());
	    user.setPhoneNumber(userRequest.getPhoneNumber());
	    user.setRole(UserRole.CUSTOMER);

	    UserEntity savedUser = userRepository.save(user);

	    UserRegisteredEvents event = new UserRegisteredEvents(
	            savedUser.getUserId(),
	            savedUser.getName(),
	            savedUser.getEmail(),
	            savedUser.getPhoneNumber(),
	            savedUser.getRole().name()
	    );

	    userProducer.publishUserRegistered(event);

	    return new UserResponse(
	            savedUser.getUserId(),
	            savedUser.getName(),
	            savedUser.getEmail(),
	            savedUser.getPhoneNumber(),
	            savedUser.getRole());
	}
	
    @Override
    public UserResponse getUserById(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole());
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole()))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long userId, UserRequest userRequest) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        UserEntity updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getUserId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(),
                updatedUser.getRole());
    }

    @Override
    public void deleteUser(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"));

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole());
    }

    @Override
    public AuthUserResponse getUserForAuthentication(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"));

        return new AuthUserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getRole());
    }

}