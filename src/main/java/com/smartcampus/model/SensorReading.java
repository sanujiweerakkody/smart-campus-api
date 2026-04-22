package com.smartcampus.model;
 
import java.io.Serializable;
import java.util.UUID;
 
/**
 * SensorReading entity representing a single measurement event from a sensor.
 * This maintains a historical log of all readings for analytical purposes.
 */
public class SensorReading implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private String id;        // Unique reading event ID (UUID)
    private long timestamp;   // Epoch time (ms) when reading was captured
    private double value;     // The actual metric value recorded by hardware
 
    /**
     * Default constructor
     */
    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
 
    /**
     * Constructor with value, auto-generates ID and timestamp
     */
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
    }
 
    /**
     * Constructor with all fields
     */
    public SensorReading(String id, long timestamp, double value) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.value = value;
    }
 
    // Getters and Setters
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public long getTimestamp() {
        return timestamp;
    }
 
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
 
    public double getValue() {
        return value;
    }
 
    public void setValue(double value) {
        this.value = value;
    }
 
    @Override
    public String toString() {
        return "SensorReading{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}