package com.Marketplace_Management.Assistant.Entities;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "summaries")
public class SummaryEntity extends UuidEntity {
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String title;
}
