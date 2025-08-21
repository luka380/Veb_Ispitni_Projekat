package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.entity.BaseUser;
import com.example.ispitni_projekat_f.model.entity.RegisteredUser;
import jakarta.ejb.Stateless;
import jakarta.validation.constraints.NotBlank;

import java.util.List;


@Stateless
public class UsersDAO extends SimpleAbstractDAO<BaseUser, Long> {

    protected UsersDAO() {
        super(BaseUser.class);
    }

    public BaseUser findByEmail(@NotBlank(message = "Email is required") String email) {
        return em.createQuery("SELECT u FROM RegisteredUser u WHERE u.email = :email", RegisteredUser.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public List<BaseUser> getUsers(int page, int size) {
        return em.createQuery("SELECT u FROM BaseUser u ORDER BY u.id", BaseUser.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}

