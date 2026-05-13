package com.TaskManagementTool.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.TaskManagementTool.Entity.UserAuth;
import com.TaskManagementTool.Enum.Permission;
import com.TaskManagementTool.Repository.UserAuthRepository;
import com.TaskManagementTool.Security.RoleBasedPermissionConfig;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAuthRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {

        UserAuth user = userRepo.findByUserOfficialEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Permission> permission =
                RoleBasedPermissionConfig.getRole_permisstion().get(user.getRole());

        List<GrantedAuthority> authorities =
                permission == null
                        ? Collections.emptyList()
                        : permission.stream()
                            .map(p -> new SimpleGrantedAuthority(p.name()))
                            .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUserOfficialEmail(),
                user.getPassword(),
                authorities
        );
    }
}	