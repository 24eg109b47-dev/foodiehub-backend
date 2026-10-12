package com.foodiehub.service;

import com.foodiehub.dto.UserProfileDto;
import com.foodiehub.dto.UserRegistrationDto;
import com.foodiehub.entity.User;
import com.foodiehub.enums.Role;
import com.foodiehub.exception.BusinessRuleViolationException;
import com.foodiehub.exception.ResourceNotFoundException;
import com.foodiehub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleViolationException("Email address is already registered.");
        }
        
        Role userRole = dto.getRole() != null ? dto.getRole() : Role.ROLE_CUSTOMER;
        if (userRole == Role.ROLE_ADMIN) {
            userRole = Role.ROLE_CUSTOMER; // Prevent self-registration as admin
        }

        User user = new User(
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getPhone(),
                userRole
        );
        return userRepository.save(user);
    }

    @Override
    public User updateProfile(Long userId, UserProfileDto dto) {
        User user = getUserById(userId);
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countCustomers() {
        return userRepository.countByRole(Role.ROLE_CUSTOMER);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOwners() {
        return userRepository.countByRole(Role.ROLE_RESTAURANT_OWNER);
    }
}
