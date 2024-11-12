package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.LoginUserDto;
import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.dtos.response.LoginResponse;
import com.cnweb.bookingapi.dtos.response.UserInfoResponse;
import com.cnweb.bookingapi.event.RegistrationCompleteEvent;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.security.jwt.JwtService;
import com.cnweb.bookingapi.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterUserDto registerUserDto,
                                           final HttpServletRequest request) throws Exception {
        User registeredUser = authenticationService.signup(registerUserDto);
        publisher.publishEvent(new RegistrationCompleteEvent(registeredUser, applicationUrl(request)));
        return ResponseEntity.ok("Success! Please check your email to complete your registration");
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(authenticationService.verifyEmail(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        UserInfoResponse userInfoResponse = new UserInfoResponse(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(), userInfoResponse);
        return ResponseEntity.ok(loginResponse);
    }

    private String applicationUrl(HttpServletRequest request) {
        String protocol = request.getServerName().equals("localhost") ? "http://" : "https://";
        return protocol + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
