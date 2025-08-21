package com.example.ispitni_projekat_f.services;

import com.example.ispitni_projekat_f.dao.UsersDAO;
import com.example.ispitni_projekat_f.model.dto.UserDTO;
import com.example.ispitni_projekat_f.model.entity.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class UserService {
    @Inject
    UsersDAO usersDAO;

    public UserDTO getUserById(long id) {
        return UserDTO.fromEntity(usersDAO.findById(id));
    }

    public UserDTO getUserByEmail(String email) {
        return UserDTO.fromEntity(usersDAO.findByEmail(email));
    }

    public List<UserDTO> getUsers(int page, int size) {
        return usersDAO.getUsers(page, size).stream().map(UserDTO::fromEntity).toList();
    }

    public UserDTO createUser(@Valid UserDTO user) {
        user = UserDTO.fromEntity(usersDAO.insert(user.toEntity()));
        return user;
    }

    public UserDTO updateUser(Long id, @Valid UserDTO user) {
        user.setId(id);
        return UserDTO.fromEntity(usersDAO.update(user.toEntity()));
    }

    public UserDTO deleteUser(Long id) {
        return UserDTO.fromEntity(usersDAO.delete(id));
    }

    public UserDTO activateUser(Long id) {
        UserDTO user = getUserById(id);
        if (user == null || user.getUserStatus() == UserStatus.ACTIVE)
            return user;
        else {
            user.setUserStatus(UserStatus.ACTIVE);
            return updateUser(id, user);
        }
    }

    public UserDTO deactivateUser(Long id) {
        UserDTO user = getUserById(id);
        if (user == null || user.getUserStatus() == UserStatus.INACTIVE)
            return user;
        else {
            user.setUserStatus(UserStatus.INACTIVE);
            return updateUser(id, user);
        }
    }
}
