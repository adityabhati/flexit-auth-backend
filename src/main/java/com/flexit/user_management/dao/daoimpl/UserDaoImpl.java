package com.flexit.user_management.dao.daoimpl;

import com.flexit.user_management.dao.UserDao;
import com.flexit.user_management.model.User;
import com.flexit.user_management.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserRepo userRepo;

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return userRepo.findById(userId);
    }
}