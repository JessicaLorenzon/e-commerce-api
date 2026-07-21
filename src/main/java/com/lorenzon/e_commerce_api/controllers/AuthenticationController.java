package com.lorenzon.e_commerce_api.controllers;

import com.lorenzon.e_commerce_api.dto.UserAuthenticationDTO;
import com.lorenzon.e_commerce_api.dto.UserLoginResponseDTO;
import com.lorenzon.e_commerce_api.dto.UserRegisterDTO;
import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.entities.user.UserRole;
import com.lorenzon.e_commerce_api.exceptions.UserAlreadyExistsException;
import com.lorenzon.e_commerce_api.infra.security.TokenService;
import com.lorenzon.e_commerce_api.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserLoginResponseDTO> register(@RequestBody @Valid UserRegisterDTO data) {
        if (userRepository.findByEmail(data.email()) != null) throw new UserAlreadyExistsException();
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.fullName(), data.email(), encryptedPassword, UserRole.USER);
        userRepository.save(newUser);
        User user = authenticate(data.email(), data.password());
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new UserLoginResponseDTO(token));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserAuthenticationDTO data) {
        User user = authenticate(data.email(), data.password());
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new UserLoginResponseDTO(token));
    }

    private User authenticate(String email, String password) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(authenticationToken);
        return (User) authentication.getPrincipal();
    }
}
