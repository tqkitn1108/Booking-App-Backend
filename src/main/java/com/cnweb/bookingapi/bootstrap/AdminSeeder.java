package com.cnweb.bookingapi.bootstrap;

import com.cnweb.bookingapi.dtos.request.RegisterUserDto;
import com.cnweb.bookingapi.model.ERole;
import com.cnweb.bookingapi.model.Role;
import com.cnweb.bookingapi.model.User;
import com.cnweb.bookingapi.repository.RoleRepository;
import com.cnweb.bookingapi.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AdminSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }
    private void createSuperAdministrator() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setFullName("Admin");
        userDto.setEmail("hust.admin@email.com");
        userDto.setPassword("12345678");
        Optional<Role> optionalRole = roleRepository.findByName(ERole.ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }
        User user = new User(userDto.getFullName(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()));
        user.setRole(optionalRole.get());
        userRepository.save(user);
    }
}
