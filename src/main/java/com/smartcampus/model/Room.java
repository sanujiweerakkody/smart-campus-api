package com.smartcampus.model;
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();
 
    // Default constructor - REQUIRED for JSON deserialization
    public Room() {
    }
 
    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = new ArrayList<>();
    }
 
    // Getters
    public String getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    public int getCapacity() {
        return capacity;
    }
 
    public List<String> getSensorIds() {
        return sensorIds;
    }
 
    // Setters
    public void setId(String id) {
        this.id = id;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
 
    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
 
    public void addSensor(String sensorId) {
        if (!this.sensorIds.contains(sensorId)) {
            this.sensorIds.add(sensorId);
        }
    }
 
    public void removeSensor(String sensorId) {
        this.sensorIds.remove(sensorId);
    }
 
    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", sensorIds=" + sensorIds +
                '}';
    }
}
 