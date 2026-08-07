package com.tfi.modules.user.service;

import com.tfi.modules.role.model.Role;
import com.tfi.modules.role.repository.RoleRepository;
import com.tfi.modules.user.model.User;
import com.tfi.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setEmail(updated.getEmail());
        existing.setUsername(updated.getUsername());
        return userRepository.save(existing);
    }

    public User assignRoles(Long userId, Set<Long> roleIds) {
        User user = findById(userId);
        Set<Role> roles = roleIds.stream()
                .map(rid -> roleRepository.findById(rid)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + rid)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}