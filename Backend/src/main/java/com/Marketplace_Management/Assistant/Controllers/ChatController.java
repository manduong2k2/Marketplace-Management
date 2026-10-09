package com.Marketplace_Management.Assistant.Controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Marketplace_Management.Assistant.Contracts.IChatService;
import com.Marketplace_Management.Assistant.DTOs.Commands.ChatCommand;
import com.Marketplace_Management.Assistant.DTOs.Request.ChatRequest;
import com.Marketplace_Management.Shared.Annotation.Auth.Authenticated;
import com.Marketplace_Management.Shared.Controllers.BaseController;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assistant")
public class ChatController extends BaseController {
    private final IChatService chatService;

    public ChatController(IChatService chatService) {
        this.chatService = chatService;
    }

    // Logged-in users only: every call spends Gemini API quota
    @Authenticated
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@Valid @RequestBody(required = true) ChatRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        ChatCommand command = ChatCommand.fromRequest(userId, req);
        return objectResponse(chatService.chat(command));
    }
}
