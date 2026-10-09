package com.Marketplace_Management.Assistant.Services;

import org.springframework.stereotype.Service;

import com.Marketplace_Management.Assistant.Clients.GeminiClient;
import com.Marketplace_Management.Assistant.Contracts.IChatService;
import com.Marketplace_Management.Assistant.DTOs.Commands.ChatCommand;
import com.Marketplace_Management.Assistant.DTOs.Response.ChatResponse;

@Service
public class ChatService implements IChatService {
    private final GeminiClient geminiClient;

    public ChatService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    /** Stateless for now: no conversation history is stored or sent, each message is answered on its own. */
    @Override
    public ChatResponse chat(ChatCommand command) {
        String reply = geminiClient.generate(command.getMessage());
        return new ChatResponse(reply, geminiClient.getModel());
    }
}
