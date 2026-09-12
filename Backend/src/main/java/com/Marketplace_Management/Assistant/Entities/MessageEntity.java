package com.Marketplace_Management.Assistant.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

@Entity 
@Table(name = "messages")
public class MessageEntity extends UuidEntity {

    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String role;
    
    @Column(nullable = false)
    private String metadata;
}
