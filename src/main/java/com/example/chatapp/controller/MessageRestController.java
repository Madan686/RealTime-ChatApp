package com.example.chatapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.service.ChatMessageService;

@RestController
public class MessageRestController {

    private final ChatMessageService chatMessageService;

    public MessageRestController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/api/messages/{roomId}")
    public List<ChatMessage> getMessagesByRoom(@PathVariable String roomId) {
        return chatMessageService.getMessagesByRoom(roomId);
    }
}