package com.Marketplace_Management.Auth.Repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListUserCommand;
import com.Marketplace_Management.Auth.DTOs.Response.UserResponse;
import com.Marketplace_Management.Auth.Entities.RoleEntity;
import com.Marketplace_Management.Auth.Entities.UserEntity;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Utils.QueryBuilder.EntityMetadataRegistry;
import com.Marketplace_Management.Shared.Utils.QueryBuilder.QueryBuilder;
import com.Marketplace_Management.Shared.Utils.QueryBuilder.QueryResults;

import tools.jackson.databind.ObjectMapper;

@Repository
public class UserRepository implements IUserRepository{
    private static final String USERS = "users";

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final EntityDomainMapper<User, UserEntity> userMapper;
    private final DSLContext dsl;
    private final EntityMetadataRegistry metadataRegistry;
    private final ObjectMapper objectMapper;

    public UserRepository(UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository,
            EntityDomainMapper<User, UserEntity> userMapper, DSLContext dsl,
            EntityMetadataRegistry metadataRegistry, ObjectMapper objectMapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.userMapper = userMapper;
        this.dsl = dsl;
        this.metadataRegistry = metadataRegistry;
        this.objectMapper = objectMapper;
    }

    public Optional<User> findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email).orElse(null);
        return entity != null ? Optional.of(userMapper.toDomain(entity)) : Optional.empty();
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userJpaRepository.findByGoogleId(googleId).map(userMapper::toDomain);
    }

    public Optional<User> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone).map(userMapper::toDomain);
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

    // ===== Admin queries (QueryBuilder) =====

    @Override
    public PaginatedResponse<UserResponse> findAll(GetListUserCommand command) {
        QueryBuilder<UserEntity> queryBuilder = baseQuery()
                .when(command.getSearch() != null, q -> {
                    String pattern = "%" + command.getSearch() + "%";
                    q.where(column("name").likeIgnoreCase(pattern).or(column("email").likeIgnoreCase(pattern)));
                })
                .when(command.getStatus() != null, q -> q.where("status", "=", command.getStatus()))
                .when(command.getRoleId() != null, q -> q.where(hasRole(command.getRoleId())))
                .orderBy(command.getSortBy(), command.getSortOrder());

        long total = queryBuilder.count();
        int offset = command.getPage() * command.getSize();

        List<UserResponse> data = queryBuilder.get(command.getSize(), offset)
                .stream()
                .map(item -> toResponse(queryBuilder, item))
                .toList();

        return new PaginatedResponse<>(data, command.getPage(), command.getSize(), total);
    }

    @Override
    public Optional<UserResponse> findResponseById(UUID id) {
        QueryBuilder<UserEntity> queryBuilder = baseQuery().where("id", "=", id);
        return queryBuilder.get().stream().findFirst().map(item -> toResponse(queryBuilder, item));
    }

    // ===== Role assignment (JPA: keeps the user_roles join table managed by Hibernate) =====

    @Override
    public List<UUID> findExistingIds(Collection<UUID> ids) {
        return userJpaRepository.findAllById(ids).stream().map(UserEntity::getId).toList();
    }

    @Override
    public List<UUID> findIdsByRole(UUID roleId) {
        return dsl.select(DSL.field(DSL.name(USERS, "id"), UUID.class))
                .from(DSL.table(DSL.name(USERS)))
                .where(hasRole(roleId))
                .and(DSL.field(DSL.name(USERS, "deleted_at")).isNull())
                .fetch(DSL.field(DSL.name(USERS, "id"), UUID.class));
    }

    @Override
    public int grantRole(UUID roleId, Collection<UUID> userIds) {
        RoleEntity role = roleJpaRepository.getReferenceById(roleId);
        List<UserEntity> changed = new ArrayList<>();

        for (UserEntity user : userJpaRepository.findAllById(userIds)) {
            // Skip users that already have the role
            if (user.getRoles().stream().noneMatch(r -> roleId.equals(r.getId()))) {
                user.getRoles().add(role);
                changed.add(user);
            }
        }

        userJpaRepository.saveAll(changed);
        return changed.size();
    }

    @Override
    public int revokeRole(UUID roleId, Collection<UUID> userIds) {
        List<UserEntity> users = userJpaRepository.findAllById(userIds);
        List<UserEntity> changed = users.stream()
                .filter(user -> user.getRoles().removeIf(r -> roleId.equals(r.getId())))
                .toList();
        userJpaRepository.saveAll(changed);
        return changed.size();
    }

    // ===== helpers =====

    private QueryBuilder<UserEntity> baseQuery() {
        QueryBuilder<UserEntity> queryBuilder = new QueryBuilder<>(dsl, metadataRegistry, objectMapper);
        return queryBuilder.query(UserEntity.class)
                // Explicit columns: never select the password hash
                .select("id", "email", "name", "avatar", "phone", "status", "googleId", "createdAt")
                .with("roles", role -> role.select("id", "name", "code"));
    }

    private org.jooq.Field<Object> column(String name) {
        return DSL.field(DSL.name(USERS, name));
    }

    /** users.id IN (SELECT user_id FROM user_roles WHERE role_id = ?): keeps every role of the matched users. */
    static Condition hasRole(UUID roleId) {
        return DSL.field(DSL.name(USERS, "id")).in(
                DSL.select(DSL.field(DSL.name("user_id")))
                        .from(DSL.table(DSL.name("user_roles")))
                        .where(DSL.field(DSL.name("role_id")).eq(roleId)));
    }

    private UserResponse toResponse(QueryBuilder<UserEntity> queryBuilder, Map<String, Object> item) {
        // Expose only whether a Google account is linked, not the Google ID itself
        item.put("googleLinked", item.remove("googleId") != null);
        return queryBuilder.to(QueryResults.normalize(item), UserResponse.class);
    }
}
