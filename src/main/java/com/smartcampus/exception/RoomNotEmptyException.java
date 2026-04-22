package com.smartcampus.exception;
 
public class RoomNotEmptyException extends Exception {
    private String roomId;
    private int sensorCount;
 
    public RoomNotEmptyException(String message) {
        super(message);
    }
 
    public RoomNotEmptyException(String message, String roomId, int sensorCount) {
        super(message);
        this.roomId = roomId;
        this.sensorCount = sensorCount;
    }
 
    public String getRoomId() {
        return roomId;
    }
 
    public int getSensorCount() {
        return sensorCount;
    }
}
 