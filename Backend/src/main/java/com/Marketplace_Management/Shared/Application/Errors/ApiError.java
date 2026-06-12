package com.Marketplace_Management.Shared.Application.Errors;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"status", "message", "cause", "className", "appTrace", "fullTrace"})
public class ApiError {
    private int status;
    private String message;
    private String className;
    private String cause;
    private List<String> fullTrace;
    private List<String> appTrace;
}
