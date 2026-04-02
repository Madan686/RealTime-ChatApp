package com.example.chatapp.service;
import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.model.RedisChatEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisPublisherService{
    public static final String CHAT_EVENTS_CHANNEL ="chat:events";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    
    public RedisPublisherService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper){
        this.stringRedisTemplate=stringRedisTemplate;
        this.objectMapper=objectMapper;
    }

    public void publish(String eventType, ChatMessage chatMessage){
        try{
            RedisChatEvent event= new RedisChatEvent(eventType,chatMessage);
            String payload=objectMapper.writeValueAsString(event);
            stringRedisTemplate.convertAndSend(CHAT_EVENTS_CHANNEL,payload);
        }catch(JsonProcessingException e){
            throw new RuntimeException("Failed to publish Redis event",e);
        }
    }
}


