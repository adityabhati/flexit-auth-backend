package com.flexit.user_management.dao;

import com.flexit.user_management.model.User;

import java.util.Optional;

public interface UserDao {
    User save(User user);

    User findByUsername(String username);

    Optional<User> findByUserId(Long userId);
}