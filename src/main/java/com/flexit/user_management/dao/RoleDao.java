package com.flexit.user_management.dao;

import com.flexit.user_management.model.Role;

import java.util.Optional;

public interface RoleDao {
    Optional<Role> findRoleById(Long id);
}