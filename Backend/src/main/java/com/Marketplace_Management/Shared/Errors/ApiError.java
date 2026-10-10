package com.Marketplace_Management.Shared.Errors;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"status", "message", "cause", "className", "appTrace", "fullTrace"})
/** Body of a 500 response; className / cause / traces are only filled with app.debug=true. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private int status;
    private String message;
    private String className;
    private String cause;
    private List<String> fullTrace;
    private List<String> appTrace;
}
