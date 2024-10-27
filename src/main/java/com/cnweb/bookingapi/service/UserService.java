package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.repository.RoleRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> allUsers() {
        return userRepository.findAll();
    }
    public User createClient(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(ERole.CLIENT);
        if (optionalRole.isEmpty()) return null;
        User client = new User(input.getFullName(), input.getEmail().toLowerCase(),
                passwordEncoder.encode(input.getPassword()));
        client.setRole(optionalRole.get());
        return userRepository.save(client);
    }
}
