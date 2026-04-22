package com.smartcampus.model;
 
import java.io.Serializable;
 
/**
 * Sensor entity representing a physical sensor device deployed in a room.
 * Sensors monitor various environmental conditions like temperature, CO2, and occupancy.
 */
public class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;
 
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String STATUS_OFFLINE = "OFFLINE";
 
    private String id;           // Unique identifier, e.g., "TEMP-001"
    private String type;         // Category: "Temperature", "Occupancy", "CO2", "Lighting"
    private String status;       // Current state: ACTIVE, MAINTENANCE, OFFLINE
    private double currentValue; // Most recent measurement recorded
    private String roomId;       // Foreign key linking to the Room
 
    /**
     * Default constructor
     */
    public Sensor() {
        this.status = STATUS_ACTIVE;
        this.currentValue = 0.0;
    }
 
    /**
     * Constructor with all fields
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status != null ? status : STATUS_ACTIVE;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }
 
    // Getters and Setters
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public double getCurrentValue() {
        return currentValue;
    }
 
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }
 
    public String getRoomId() {
        return roomId;
    }
 
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
 
    /**
     * Check if the sensor is currently available (not in maintenance or offline)
     */
    public boolean isAvailable() {
        return STATUS_ACTIVE.equals(this.status);
    }
 
    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", currentValue=" + currentValue +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}