package com.Marketplace_Management.Auth.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.Entities.UserEntity;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Shared.Contracts.IMapper;

@Repository
public class UserRepository implements IUserRepository{
    private final UserJpaRepository userJpaRepository;
    private final IMapper<User, UserEntity> userMapper;
    
    public UserRepository(UserJpaRepository userJpaRepository, IMapper<User, UserEntity> userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    public Optional<User> findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email).orElse(null);
        return entity != null ? Optional.of(userMapper.toDomain(entity)) : Optional.empty();
    }

    public Optional<User> findById(UUID id) {
        UserEntity entity = userJpaRepository.findById(id).orElse(null);
        return entity != null ? Optional.of(userMapper.toDomain(entity)) : Optional.empty();
    }

    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }

    public void delete(User user) {
        userJpaRepository.deleteById(user.getId());
    }
}
