package com.example.chatapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.repository.ChatMessageRepository;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}