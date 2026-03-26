package com.example.chatapp.dto;

public class JoinRoomRequest {

    private String roomId;
    private String password;

    public JoinRoomRequest() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}