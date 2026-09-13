package com.Marketplace_Management.Assistant.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data 
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor 
@NoArgsConstructor
@SuperBuilder
public class Message extends AggregateRoot<UUID> {
    private String content;
    private String role;
    private String metadata;
}
