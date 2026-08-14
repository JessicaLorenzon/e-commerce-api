package com.lorenzon.e_commerce_api.factories;

import com.lorenzon.e_commerce_api.entities.user.User;

public class UserFactory {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ana@ana.com");
        return user;
    }
}
