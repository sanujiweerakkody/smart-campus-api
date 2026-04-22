package com.smartcampus.exception;
 
public class SensorUnavailableException extends Exception {
    private String sensorId;
    private String status;
 
    public SensorUnavailableException(String message) {
        super(message);
    }
 
    public SensorUnavailableException(String message, String sensorId, String status) {
        super(message);
        this.sensorId = sensorId;
        this.status = status;
    }
 
    public String getSensorId() {
        return sensorId;
    }
 
    public String getStatus() {
        return status;
    }
}