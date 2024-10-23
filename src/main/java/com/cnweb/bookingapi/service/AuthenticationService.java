package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.LoginUserDto;
import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.repository.RoleRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }
    public User signup(RegisterUserDto input) throws Exception {
        String email = input.getEmail().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email has been used!");
        }
        Optional<Role> optionalRole = roleRepository.findByName(ERole.USER);
        if (optionalRole.isEmpty()) return null;
        User user = new User(input.getFullName(), email, passwordEncoder.encode(input.getPassword()));
        user.setRole(optionalRole.get());
        return userRepository.save(user);
    }
    public User authenticate(LoginUserDto input) {
        String email = input.getEmail().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, input.getPassword()));
        return userRepository.findByEmail(email).orElseThrow();
    }
}
