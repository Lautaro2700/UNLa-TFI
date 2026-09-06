package com.tfi.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tfi.auth.modules.permission.model.Permission;
import com.tfi.auth.modules.permission.repository.PermissionRepository;
import com.tfi.auth.modules.role.model.Role;
import com.tfi.auth.modules.role.repository.RoleRepository;
import com.tfi.auth.modules.user.model.User;
import com.tfi.auth.modules.user.repository.UserRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Solo carga datos si la base está vacía
        if (permissionRepository.count() > 0) return;

        // Permisos
        Permission create = permissionRepository.save(Permission.builder().name("CREATE").build());
        Permission read   = permissionRepository.save(Permission.builder().name("READ").build());
        Permission update = permissionRepository.save(Permission.builder().name("UPDATE").build());
        Permission delete = permissionRepository.save(Permission.builder().name("DELETE").build());

        // Roles
        Role adminRole = roleRepository.save(Role.builder()
                .name("ADMIN")
                .permissions(Set.of(create, read, update, delete))
                .build());

        Role userRole = roleRepository.save(Role.builder()
                .name("USER")
                .permissions(Set.of(read))
                .build());

        // Usuarios
        userRepository.save(User.builder()
                .username("persona1")
                .email("persona1@tfi.com")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(adminRole))
                .build());

        userRepository.save(User.builder()
                .username("persona2")
                .email("persona2@tfi.com")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(userRole))
                .build());

        System.out.println(">>> Datos iniciales cargados correctamente");
    }
}