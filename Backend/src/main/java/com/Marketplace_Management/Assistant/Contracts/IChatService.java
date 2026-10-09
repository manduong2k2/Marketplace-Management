package com.Marketplace_Management.Assistant.Contracts;

import com.Marketplace_Management.Assistant.DTOs.Commands.ChatCommand;
import com.Marketplace_Management.Assistant.DTOs.Response.ChatResponse;

public interface IChatService {
    ChatResponse chat(ChatCommand command);
}
