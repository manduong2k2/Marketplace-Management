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

import com.Marketplace_Management.Auth.Contracts.IRoleService;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListRoleCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.SaveRoleCommand;
import com.Marketplace_Management.Auth.DTOs.Request.CreateRoleRequest;
import com.Marketplace_Management.Auth.DTOs.Request.GetListRoleRequest;
import com.Marketplace_Management.Auth.DTOs.Request.GetListUserRequest;
import com.Marketplace_Management.Auth.DTOs.Request.UpdateRoleRequest;
import com.Marketplace_Management.Shared.Constants.UserRole;
import com.Marketplace_Management.Shared.Controllers.BaseController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
public class RoleController extends BaseController {
    private final IRoleService roleService;

    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    /** ?page=&size=&sortBy=&sortOrder=&search= (each role includes usersCount) */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(@Valid @ModelAttribute GetListRoleRequest request) {
        return paginatedResponse(roleService.getRoles(GetListRoleCommand.fromRequest(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> details(@PathVariable UUID id) {
        return objectResponse(roleService.getRole(id));
    }

    /** Users having this role: same paging/search/status options as GET /api/users. */
    @GetMapping("/{id}/users")
    public ResponseEntity<Map<String, Object>> users(@PathVariable UUID id, @Valid @ModelAttribute GetListUserRequest request) {
        return paginatedResponse(roleService.getRoleUsers(GetListUserCommand.forRole(id, request)));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateRoleRequest request) {
        return createdResponse(roleService.createRole(SaveRoleCommand.fromRequest(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @Valid @RequestBody UpdateRoleRequest request) {
        return objectResponse(roleService.updateRole(id, SaveRoleCommand.fromRequest(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return objectResponse(Map.of("id", id));
    }
}
