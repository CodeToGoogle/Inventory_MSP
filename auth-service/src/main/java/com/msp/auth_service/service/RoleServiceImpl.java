package com.msp.auth_service.service;

import com.msp.auth_service.entity.UserRole;
import com.msp.auth_service.repository.RoleRepository;
import com.msp.auth_service.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<String> getRolesForUser(Integer userId) {
        List<UserRole> urs = userRoleRepository.findByUser_UserId(userId);
        return urs.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());
    }
}
