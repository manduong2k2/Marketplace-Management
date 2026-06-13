package com.Marketplace_Management.Shared.Application.DTO.Responses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "url"})
public class FileResponse {
    private UUID id;
    private String url;
}
