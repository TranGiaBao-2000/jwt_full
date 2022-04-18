package com.example.jwt.controller;


import com.example.jwt.dto.LoginRequest;
import com.example.jwt.jwt.TokenHandler;
import com.example.jwt.user.CustomerDetails;
import com.example.jwt.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@PreAuthorize("isAuthenticated()")
public class ApiController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private TokenHandler tokenHandler;


    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // nếu chạy đến đây mà ok thì thông tin hợp lệ
        // set thông tin vào security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenHandler.generateToken((CustomerDetails) authentication.getPrincipal());
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/authen")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity authen(){
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/noauthen")
    public ResponseEntity noAuthen(){
        return ResponseEntity.ok("ok");
    }
}
