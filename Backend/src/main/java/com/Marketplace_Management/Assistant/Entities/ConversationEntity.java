package com.Marketplace_Management.Assistant.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

@Entity
@Table(name = "conversations")
public class ConversationEntity extends UuidEntity{

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "status", nullable = false)
    private String status;
}
