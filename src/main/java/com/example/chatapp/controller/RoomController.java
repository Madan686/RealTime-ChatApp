package com.example.chatapp.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.chatapp.dto.CreateRoomRequest;
import com.example.chatapp.dto.JoinRoomRequest;
import com.example.chatapp.model.ChatRoom;
import com.example.chatapp.repository.ChatRoomRepository;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder passwordEncoder;

    public RoomController(ChatRoomRepository chatRoomRepository, PasswordEncoder passwordEncoder) {
        this.chatRoomRepository = chatRoomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request, Authentication authentication) {
        if (request.getRoomId() == null || request.getRoomId().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Room ID and password are required");
        }

        if (chatRoomRepository.existsByRoomId(request.getRoomId())) {
            return ResponseEntity.badRequest().body("Room ID already exists");
        }

        ChatRoom room = new ChatRoom();
        room.setRoomId(request.getRoomId().trim());
        room.setRoomName(request.getRoomName() != null ? request.getRoomName().trim() : "");
        room.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));
        room.setCreatedBy(authentication != null ? authentication.getName() : "unknown");
        room.setCreatedAt(LocalDateTime.now());

        chatRoomRepository.save(room);

        return ResponseEntity.ok("Room created successfully");
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody JoinRoomRequest request) {
        if (request.getRoomId() == null || request.getRoomId().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Room ID and password are required");
        }

        ChatRoom room = chatRoomRepository.findByRoomId(request.getRoomId().trim())
                .orElse(null);

        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        boolean matches = passwordEncoder.matches(
                request.getPassword().trim(),
                room.getPasswordHash()
        );

        if (!matches) {
            return ResponseEntity.status(401).body("Invalid room password");
        }

        return ResponseEntity.ok(room);
    }
}