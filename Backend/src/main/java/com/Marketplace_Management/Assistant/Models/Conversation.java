package com.Marketplace_Management.Assistant.Models;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data 
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor 
@NoArgsConstructor
@SuperBuilder
public class Conversation extends AggregateRoot<UUID> {

    private UUID userId;
    private String title;
    private String status;
}
