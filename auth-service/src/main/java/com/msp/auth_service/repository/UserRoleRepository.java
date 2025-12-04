package com.msp.auth_service.repository;

import com.msp.auth_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    List<UserRole> findByUserID(Integer userId);
}
