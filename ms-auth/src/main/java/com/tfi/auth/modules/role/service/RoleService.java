package com.tfi.auth.modules.role.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tfi.auth.modules.permission.model.Permission;
import com.tfi.auth.modules.permission.repository.PermissionRepository;
import com.tfi.auth.modules.role.model.Role;
import com.tfi.auth.modules.role.repository.RoleRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public Role create(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Role already exists: " + role.getName());
        }
        return roleRepository.save(role);
    }

    public Role update(Long id, Role updated) {
        Role existing = findById(id);
        existing.setName(updated.getName());
        return roleRepository.save(existing);
    }

    public Role assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = findById(roleId);
        Set<Permission> permissions = permissionIds.stream()
                .map(pid -> permissionRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + pid)))
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}
