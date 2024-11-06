package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.model.token.VerificationToken;
import com.cnweb.bookingapi.repository.RoleRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import com.cnweb.bookingapi.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public User createClient(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(ERole.HOTEL);
        if (optionalRole.isEmpty()) return null;
        User client = new User(input.getFullName(), input.getEmail().toLowerCase(),
                passwordEncoder.encode(input.getPassword()));
        client.setRole(optionalRole.get());
        return userRepository.save(client);
    }

    public void saveUserVerificationToken(User user, String token) {
        var verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }
}
