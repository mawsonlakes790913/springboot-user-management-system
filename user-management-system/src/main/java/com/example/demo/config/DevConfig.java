package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.domain.service.impl.UserServiceImpl2;
import com.example.demo.user.repository.UserRepository;

@Configuration
@Profile("dev")
public class DevConfig {

    @Bean
    UserService userService(UserRepository repository, PasswordEncoder encoder) {
        return new UserServiceImpl2(repository, encoder);
    }
}
