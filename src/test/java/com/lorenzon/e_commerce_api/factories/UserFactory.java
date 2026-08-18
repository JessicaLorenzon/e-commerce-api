package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.user.User;
import com.lorenzon.e_commerce_api.entities.user.UserRole;

public class UserFactory {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ana@ana.com");
        user.setRole(UserRole.USER);
        return user;
    }

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("other@other.com");
        user.setRole(UserRole.USER);
        return user;
    }

    public static User createAdmin() {
        User admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@admin.com");
        admin.setRole(UserRole.ADMIN);
        return admin;
    }
}
