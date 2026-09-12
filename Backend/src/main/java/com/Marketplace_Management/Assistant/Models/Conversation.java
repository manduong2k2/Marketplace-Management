package com.Marketplace_Management.Assistant.Models;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data 
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Conversation extends AggregateRoot<UUID> {

    private UUID userId;
    private String title;
    private String status;
    
    public Conversation(UUID id, UUID userId, String title, String status) {
        super(id);
        this.userId = userId;
        this.title = title;
        this.status = status;
    }
}
