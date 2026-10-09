package com.Marketplace_Management.Auth.Controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Marketplace_Management.Auth.Contracts.IUserService;
import com.Marketplace_Management.Auth.DTOs.Commands.CreateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.UpdateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Request.CreateUserRequest;
import com.Marketplace_Management.Auth.DTOs.Request.GetListUserRequest;
import com.Marketplace_Management.Auth.DTOs.Request.UpdateUserRequest;
import com.Marketplace_Management.Auth.DTOs.Request.UserIdsRequest;
import com.Marketplace_Management.Shared.Constants.UserRole;
import com.Marketplace_Management.Shared.Controllers.BaseController;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
public class UserController extends BaseController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /** ?page=&size=&sortBy=&sortOrder=&search=&roleId=&status= */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@Valid @ModelAttribute GetListUserRequest request) {
        return paginatedResponse(userService.getUsers(GetListUserCommand.fromRequest(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> details(@PathVariable UUID id) {
        return objectResponse(userService.getUser(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateUserRequest request) {
        return createdResponse(userService.createUser(CreateUserCommand.fromRequest(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return objectResponse(userService.updateUser(id, UpdateUserCommand.fromRequest(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        userService.deleteUser(id, SecurityUtils.currentUserId());
        return objectResponse(Map.of("id", id));
    }

    /** Body: { "userIds": [...] }. Users that already have the role are skipped. */
    @PostMapping("/role-grant/{roleId}")
    public ResponseEntity<Map<String, Object>> grantRole(@PathVariable UUID roleId, @Valid @RequestBody UserIdsRequest request) {
        int affected = userService.grantRole(roleId, request.getUserIds());
        return objectResponse(Map.of("roleId", roleId, "affected", affected));
    }

    /** Body: { "userIds": [...] }. Users that do not have the role are skipped. */
    @PostMapping("/role-revoke/{roleId}")
    public ResponseEntity<Map<String, Object>> revokeRole(@PathVariable UUID roleId, @Valid @RequestBody UserIdsRequest request) {
        int affected = userService.revokeRole(roleId, request.getUserIds(), SecurityUtils.currentUserId());
        return objectResponse(Map.of("roleId", roleId, "affected", affected));
    }
}
