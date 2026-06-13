package com.Marketplace_Management.Auth.Contracts;
import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Auth.Models.User;

public interface IUserRepository{
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
    void delete(User user);
}
