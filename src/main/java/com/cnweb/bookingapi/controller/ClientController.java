package com.cnweb.bookingapi.controller;

import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final UserService userService;
    public ClientController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<User> createClient(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User createdClient = userService.createClient(registerUserDto);
        return ResponseEntity.ok(createdClient);
    }
}
