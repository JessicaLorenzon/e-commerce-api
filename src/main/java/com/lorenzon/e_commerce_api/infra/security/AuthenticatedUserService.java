package com.lorenzon.e_commerce_api.infra.security;

import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticatedUserService {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedUser() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return (User) userRepository.findByEmail(email);
    }
}
