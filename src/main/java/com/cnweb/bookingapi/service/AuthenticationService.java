package com.cnweb.bookingapi.service;

import com.cnweb.bookingapi.dtos.request.LoginUserDto;
import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.AuthProvider;
import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.model.token.VerificationToken;
import com.cnweb.bookingapi.repository.RoleRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import com.cnweb.bookingapi.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User signup(RegisterUserDto input) throws Exception {
        String email = input.getEmail().toLowerCase();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().isVerified()) {
            throw new Exception("User with email " + input.getEmail() + " already exists");
        } else user.ifPresent(userRepository::delete);
        Optional<Role> optionalRole = roleRepository.findByName(input.getRole() == null ? ERole.USER : input.getRole());
        if (optionalRole.isEmpty()) return null;
        User newUser = new User(input.getFullName(), email, passwordEncoder.encode(input.getPassword()));
        newUser.setRole(optionalRole.get());
        newUser.setProvider(AuthProvider.local);
        return userRepository.save(newUser);
    }

    public User authenticate(LoginUserDto input) {
        String email = input.getEmail().toLowerCase();
        String password = input.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userRepository.findByEmail(email).orElseThrow();
    }

    public String verifyEmail(String token) {
        Optional<VerificationToken> theToken = tokenRepository.findByToken(token);
        if (theToken.isEmpty()) return "Invalid verification token";
        User user = theToken.get().getUser();
        if (user.isVerified()) return "This account has been verified, please login.";
        if (theToken.get().getExpirationTime().getTime() < System.currentTimeMillis()) {
            tokenRepository.delete(theToken.get());
            return "Token already expired";
        }
        user.setVerified(true);
        userRepository.save(user);
        return "Email verified successfully. Now you can login to your account.";
    }
}
