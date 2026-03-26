package com.example.chatapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.chatapp.model.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findByRoomId(String roomId);
    boolean existsByRoomId(String roomId);
}