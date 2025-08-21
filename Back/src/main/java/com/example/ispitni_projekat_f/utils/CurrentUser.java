package com.example.ispitni_projekat_f.utils;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CurrentUser {
    private UserDTO user;

    public UserDTO get() {
        return user;
    }

    public void set(UserDTO user) {
        this.user = user;
    }
}
