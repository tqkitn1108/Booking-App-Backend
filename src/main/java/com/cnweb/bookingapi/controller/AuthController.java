package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.LoginUserDto;
import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.dtos.response.LoginResponse;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.security.jwt.JwtService;
import com.cnweb.bookingapi.service.AuthenticationService;
import com.cnweb.bookingapi.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody @Valid RegisterUserDto registerUserDto) throws Exception {
        User registeredUser = authenticationService.signup(registerUserDto);
        String subject = "Confirm your account";
        String body = "Your account has been created on our platform";
        emailService.sendEmail(registerUserDto.getEmail(), subject, body);
        return ResponseEntity.ok(registeredUser);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
