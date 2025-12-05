package com.msp.auth_service.service;

import java.util.List;

public interface RoleService {
    List<String> getRolesForUser(Integer userId);
}

