package com.example.chatapp.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.MessageType;
import com.example.chatapp.service.ChatMessageService;
import com.example.chatapp.service.RedisPublisherService;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final RedisPublisherService redisPublisherService;

    public ChatController(ChatMessageService chatMessageService,
                          RedisPublisherService redisPublisherService) {
        this.chatMessageService = chatMessageService;
        this.redisPublisherService = redisPublisherService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageService.saveMessage(message);

        // 🔥 Send via Redis instead of direct WebSocket
        redisPublisherService.publish("ROOM_MESSAGE", message);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage message) {
        message.setType(MessageType.JOIN);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(message.getSender() + " joined the room");

        chatMessageService.saveMessage(message);

        redisPublisherService.publish("JOIN", message);
    }

    @MessageMapping("/chat.leaveUser")
    public void leaveUser(@Payload ChatMessage message) {
        message.setType(MessageType.LEAVE);
        message.setTimestamp(LocalDateTime.now());
        message.setContent(message.getSender() + " left the room");

        chatMessageService.saveMessage(message);

        redisPublisherService.publish("LEAVE", message);
    }

    @MessageMapping("/chat.privateMessage")
    public void privateMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatMessageService.saveMessage(message);

        redisPublisherService.publish("PRIVATE_MESSAGE", message);
    }
}