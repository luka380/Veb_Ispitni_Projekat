package com.example.ispitni_projekat_f.utils;

import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.entity.UserStatus;
import com.example.ispitni_projekat_f.model.entity.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final String SECRET = "gsdfsdfgdsfgsdgjhgtdhyjmrtukjtyjeryhjfrgfhetryjertyjdfghnedftrhymetyrmnn";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(UserDTO user) {
        if (user.getUserType() == UserType.ANONYMOUS) {
            return Jwts.builder()
                    .setIssuedAt(new Date())
                    .claim("userId", user.getId())
                    .claim("userType", user.getUserType())
                    .claim("userStatus", user.getUserStatus())
                    .signWith(KEY, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            return Jwts.builder()
                    .setIssuedAt(new Date())
                    .claim("userId", user.getId())
                    .claim("userType", user.getUserType())
                    .claim("userStatus", user.getUserStatus())
                    .claim("userName", user.getFirstName() + " " + user.getLastName())
                    .signWith(KEY, SignatureAlgorithm.HS256)
                    .compact();
        }
    }

    public static UserDTO parseToken(String token) throws JwtException {
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        Long userId = claims.get("userId", Long.class);
        String userType = claims.get("userType", String.class);
        String userStatus = claims.get("userStatus", String.class);
        String userName = (String) claims.getOrDefault("userName", " ");
        Date issuedAt = claims.getIssuedAt();

        return new UserDTO(userId, "", "", userName.split(" ")[0], userName.split(" ")[1], UserType.valueOf(userType), UserStatus.valueOf(userStatus), 0L);
    }
}
