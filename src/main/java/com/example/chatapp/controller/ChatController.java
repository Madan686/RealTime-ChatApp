package com.example.chatapp.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.MessageType;
import com.example.chatapp.service.ChatMessageService;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageService.saveMessage(message);

        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage message) {
        message.setType(MessageType.JOIN);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(message.getSender() + " joined the room");

        chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat.leaveUser")
    public void leaveUser(@Payload ChatMessage message) {
        message.setType(MessageType.LEAVE);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(message.getSender() + " left the room");

        chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat.privateMessage")
    public void privateMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageService.saveMessage(message);

        messagingTemplate.convertAndSend("/topic/private/" + message.getRecipient(), message);
        messagingTemplate.convertAndSend("/topic/private/" + message.getSender(), message);
    }
}