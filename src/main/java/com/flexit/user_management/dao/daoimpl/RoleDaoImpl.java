package com.flexit.user_management.dao.daoimpl;

import com.flexit.user_management.dao.RoleDao;
import com.flexit.user_management.model.Role;
import com.flexit.user_management.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao {
    private final RoleRepo roleRepo;

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepo.findById(id);
    }
}