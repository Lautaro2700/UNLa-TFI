package com.tfi.auth.modules.permission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tfi.auth.modules.permission.model.Permission;
import com.tfi.auth.modules.permission.repository.PermissionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
    }

    public Permission create(Permission permission) {
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new RuntimeException("Permission already exists: " + permission.getName());
        }
        return permissionRepository.save(permission);
    }

    public Permission update(Long id, Permission updated) {
        Permission existing = findById(id);
        existing.setName(updated.getName());
        return permissionRepository.save(existing);
    }

    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}
