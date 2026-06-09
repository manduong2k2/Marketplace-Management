package com.StoreManagement.Shared.Application.Controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;

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

    protected ResponseEntity<Map<String, Object>> simpleResponse(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    protected ResponseEntity<Map<String, Object>> paginatedResponse(PaginatedResponse<?> response){
        HashMap<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("data", response.getData());
        responseData.put("pagination", new HashMap<String, Object>() {{
            put("currentPage", response.getCurrentPage());
            put("pageSize", response.getPageSize());
            put("totalElements", response.getTotalElements());
            put("totalPages", response.getTotalPages());
            put("hasNext", response.isHasNext());
            put("hasPrevious", response.isHasPrevious());
        }});
        return ResponseEntity.ok().body(responseData);
    }
}
