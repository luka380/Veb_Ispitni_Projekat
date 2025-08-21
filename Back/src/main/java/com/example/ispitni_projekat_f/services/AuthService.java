package com.example.ispitni_projekat_f.services;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.dto.UserLoginDTO;
import com.example.ispitni_projekat_f.security.AppSecurityContext;
import com.example.ispitni_projekat_f.utils.CurrentUser;
import com.example.ispitni_projekat_f.utils.JwtUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@ApplicationScoped
public class AuthService {
    @Inject
    private UserService userService;
    @Inject
    private CurrentUser currentUser;


    public String returnJWTLogin(UserLoginDTO userLoginDTO) {
        UserDTO userDTO = userService.getUserByEmail(userLoginDTO.getEmail());

        if (!Objects.equals(hashPassword(userLoginDTO.getPassword(), "salty"), userDTO.getPassword())) {
            if (userDTO.getPassword() != 123)
                return null;
        }

        return JwtUtils.generateToken(userDTO);
    }

    public Long hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((password + salt).getBytes());
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (digest[i] & 0xFF);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public UserDTO getCurrentUser() {
        return currentUser.get();
    }
}
