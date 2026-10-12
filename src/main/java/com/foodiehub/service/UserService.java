package com.foodiehub.service;

import com.foodiehub.dto.UserProfileDto;
import com.foodiehub.dto.UserRegistrationDto;
import com.foodiehub.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(UserRegistrationDto dto);
    User updateProfile(Long userId, UserProfileDto dto);
    Optional<User> findByEmail(String email);
    User getUserById(Long id);
    List<User> getAllUsers();
    User toggleUserStatus(Long userId);
    long countTotalUsers();
    long countCustomers();
    long countOwners();
}
