package com.Marketplace_Management.Shared.Controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;

public abstract class BaseController {

    protected ResponseEntity<Map<String, Object>> objectResponse(Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.status(data == null ? 404 : 200).body(response);
    }

    protected ResponseEntity<Map<String, Object>> createdResponse(Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.status(201).body(response);
    }

    protected ResponseEntity<Map<String, Object>> successResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", message);
        return ResponseEntity.ok().body(response);
    }

    protected ResponseEntity<Map<String, Object>> badRequestResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    /** { success, data, pagination: { currentPage, pageSize, totalElements, totalPages, hasNext, hasPrevious } } */
    protected ResponseEntity<Map<String, Object>> paginatedResponse(PaginatedResponse<?> page) {
        Map<String, Object> pagination = new LinkedHashMap<>();
        pagination.put("currentPage", page.getCurrentPage());
        pagination.put("pageSize", page.getPageSize());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("hasNext", page.isHasNext());
        pagination.put("hasPrevious", page.isHasPrevious());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("data", page.getData());
        response.put("pagination", pagination);
        return ResponseEntity.ok().body(response);
    }
}
