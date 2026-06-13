package com.Marketplace_Management.Shared.Security;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserPrincipal {
    private UUID id;
    private List<SimpleGrantedAuthority> roles;
}
