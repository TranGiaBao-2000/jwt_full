package com.example.jwt;

import com.example.jwt.user.User;
import com.example.jwt.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JwtApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JwtApplication.class, args);
    }

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("giabao");
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode("giabao"));
        userRepository.save(user);

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("giabao2");
        user2.setRole("ROLE_ADMIN");
        user2.setPassword(passwordEncoder.encode("giabao2"));
        userRepository.save(user2);
    }
}
