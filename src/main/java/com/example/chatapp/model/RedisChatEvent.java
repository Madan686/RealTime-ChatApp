package com.example.chatapp.model;

public class RedisChatEvent {
    private String eventType;
    private ChatMessage chatMessage;
    public RedisChatEvent() {
    }

    public RedisChatEvent(String eventType, ChatMessage chatMessage){
        this.eventType=eventType;
        this.chatMessage=chatMessage;
    }
    public String getEventType(){
        return eventType;
    }
    public void setEventType(String eventType){
        this.eventType=eventType;
    }
    public ChatMessage getChatMessage(){
        return chatMessage;
    }
    public void setChatMessage(ChatMessage chatMessage){
        this.chatMessage=chatMessage;
    }
}
