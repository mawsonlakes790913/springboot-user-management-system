package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.user.domain.service.UserService;
import com.example.demo.user.domain.service.impl.UserServiceImpl;
import com.example.demo.user.repository.UserMapper;

@Configuration
@Profile("local")
public class LocalConfig {

    @Bean
    UserService userService(UserMapper mapper, PasswordEncoder encoder) {
        return new UserServiceImpl(mapper, encoder);
    }
}
