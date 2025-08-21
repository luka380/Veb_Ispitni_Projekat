package com.example.ispitni_projekat_f.model.dto;

import com.example.ispitni_projekat_f.model.entity.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    protected long id;
    private String email;
    private String name;
    private String firstName;
    private String lastName;
    private UserType userType = UserType.EVENT_CREATOR;
    private UserStatus userStatus = UserStatus.ACTIVE;
    private Long password;

    @JsonSetter("password")
    public void setPassword(String password){
        this.password = hashPassword(password, "salty");
    }

    public UserDTO(long id) {
        this.id = id;
    }

    public static UserDTO fromEntity(BaseUser user) {
        switch (user) {
            case RegisteredUser registeredUser -> {
                UserDTO dto = new UserDTO();
                dto.id = registeredUser.getId();
                dto.email = registeredUser.getEmail();
                dto.firstName = registeredUser.getFirstname();
                dto.lastName = registeredUser.getLastname();
                dto.userType = registeredUser.getUserType();
                dto.userStatus = registeredUser.getUserStatus();
                dto.password = registeredUser.getPassword();
                return dto;
            }
            case AnonimousUser anonymousUser -> {
                UserDTO dto = new UserDTO();
                dto.id = anonymousUser.getId();
                dto.email = anonymousUser.getEmail();
                dto.name = anonymousUser.getName();
                dto.userType = anonymousUser.getUserType();
                dto.userStatus = anonymousUser.getUserStatus();
                return dto;
            }
            case null, default -> {
                return null;
            }
        }
    }

    public BaseUser toEntity() {
        if (this.userType == UserType.ANONYMOUS) {
            AnonimousUser anonimousUser = new AnonimousUser();
            anonimousUser.setId(id);
            anonimousUser.setEmail(email);
            anonimousUser.setName(name);
            anonimousUser.setUserStatus(userStatus);
            anonimousUser.setUserType(userType);
            return anonimousUser;
        } else {
            RegisteredUser user = new RegisteredUser();
            user.setId(this.id);
            user.setEmail(this.email);
            user.setFirstname(this.firstName);
            user.setLastname(this.lastName);
            user.setUserType(this.userType);
            user.setUserStatus(this.userStatus);
            user.setPassword(this.password);
            return user;
        }
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
}
