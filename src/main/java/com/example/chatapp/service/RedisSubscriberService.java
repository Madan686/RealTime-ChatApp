package com.example.chatapp.service;

import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.RedisChatEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSubscriberService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriberService(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper){
        this.messagingTemplate=messagingTemplate;
        this.objectMapper=objectMapper;
    }
    public void handleMessage(String message){
        try{
            RedisChatEvent event=objectMapper.readValue(message, RedisChatEvent.class);
            ChatMessage chatMessage=event.getChatMessage();

            if(chatMessage == null || event.getEventType()==null){
                return;
            }

            switch(event.getEventType()){
                case "ROOM_MESSAGE":
                case "JOIN":
                case "LEAVE":
                    messagingTemplate.convertAndSend(
                        "/topic/room/" + chatMessage.getRoomId(),
                         chatMessage 
                        );
                        break;

                case "PRIVATE_MESSAGE":
                    messagingTemplate.convertAndSend("/topic/private/" + chatMessage.getRecipient(), chatMessage );
                    messagingTemplate.convertAndSend("/topic/private/" + chatMessage.getSender(), chatMessage);
                    break;

                default:
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
