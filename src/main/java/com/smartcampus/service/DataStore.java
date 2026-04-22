package com.smartcampus.service;
 
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
 
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * Singleton data store managing all entities in memory.
 * Uses ConcurrentHashMap for thread-safe access without explicit synchronization.
 * 
 * Since JAX-RS creates a new Resource instance per request, all resource classes
 * access this shared singleton to prevent data loss and ensure consistency.
 */
public class DataStore {
    private static DataStore instance;
    
    // Thread-safe collections to prevent race conditions
    private final Map<String, Room> rooms;
    private final Map<String, Sensor> sensors;
    private final Map<String, List<SensorReading>> sensorReadings; // Key: sensorId, Value: list of readings
 
    /**
     * Private constructor to enforce singleton pattern
     */
    private DataStore() {
        this.rooms = new ConcurrentHashMap<>();
        this.sensors = new ConcurrentHashMap<>();
        this.sensorReadings = new ConcurrentHashMap<>();
        
        // Initialize with sample data for testing
        initializeData();
    }
 
    /**
     * Get singleton instance (thread-safe lazy initialization)
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }
 
    // ==================== ROOM OPERATIONS ====================
 
    public Room createRoom(Room room) {
        if (rooms.containsKey(room.getId())) {
            return null; // Room already exists
        }
        rooms.put(room.getId(), room);
        return room;
    }
 
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
 
    public Room getRoomById(String roomId) {
        return rooms.get(roomId);
    }
 
    public boolean updateRoom(String roomId, Room room) {
        if (!rooms.containsKey(roomId)) {
            return false;
        }
        rooms.put(roomId, room);
        return true;
    }
 
    public boolean deleteRoom(String roomId) {
        return rooms.remove(roomId) != null;
    }
 
    public boolean roomExists(String roomId) {
        return rooms.containsKey(roomId);
    }
 
    // ==================== SENSOR OPERATIONS ====================
 
    public Sensor createSensor(Sensor sensor) {
        if (sensors.containsKey(sensor.getId())) {
            return null; // Sensor already exists
        }
        sensors.put(sensor.getId(), sensor);
        
        // Add sensor reference to room
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.addSensor(sensor.getId());
        }
        
        // Initialize empty readings list for this sensor
        sensorReadings.putIfAbsent(sensor.getId(), Collections.synchronizedList(new ArrayList<>()));
        
        return sensor;
    }
 
    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }
 
    public List<Sensor> getSensorsByType(String type) {
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType().equalsIgnoreCase(type)) {
                filtered.add(sensor);
            }
        }
        return filtered;
    }
 
    public Sensor getSensorById(String sensorId) {
        return sensors.get(sensorId);
    }
 
    public boolean updateSensor(String sensorId, Sensor sensor) {
        if (!sensors.containsKey(sensorId)) {
            return false;
        }
        sensors.put(sensorId, sensor);
        return true;
    }
 
    public boolean deleteSensor(String sensorId) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            return false;
        }
        
        // Remove sensor reference from room
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.removeSensor(sensorId);
        }
        
        // Remove readings
        sensorReadings.remove(sensorId);
        
        return sensors.remove(sensorId) != null;
    }
 
    public boolean sensorExists(String sensorId) {
        return sensors.containsKey(sensorId);
    }
 
    public List<Sensor> getSensorsByRoomId(String roomId) {
        List<Sensor> roomSensors = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getRoomId().equals(roomId)) {
                roomSensors.add(sensor);
            }
        }
        return roomSensors;
    }
 
    // ==================== SENSOR READING OPERATIONS ====================
 
    public SensorReading addReading(String sensorId, SensorReading reading) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            readings = Collections.synchronizedList(new ArrayList<>());
            sensorReadings.put(sensorId, readings);
        }
        readings.add(reading);
        return reading;
    }
 
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        return readings != null ? new ArrayList<>(readings) : new ArrayList<>();
    }
 
    public SensorReading getReadingById(String sensorId, String readingId) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            return null;
        }
        for (SensorReading reading : readings) {
            if (reading.getId().equals(readingId)) {
                return reading;
            }
        }
        return null;
    }
 
    // ==================== HELPER METHODS ====================
 
    /**
     * Initialize sample data for testing
     */
    private void initializeData() {
        // Create sample rooms
        Room room1 = new Room("LIB-301", "Library Quiet Study", 20);
        Room room2 = new Room("LAB-102", "Computer Lab", 30);
        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);
 
        // Create sample sensors
        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor sensor2 = new Sensor("CO2-001", "CO2", "ACTIVE", 450.0, "LIB-301");
        Sensor sensor3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 15.0, "LAB-102");
        
        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);
        sensors.put(sensor3.getId(), sensor3);
        
        // Add sensor references to rooms
        room1.addSensor(sensor1.getId());
        room1.addSensor(sensor2.getId());
        room2.addSensor(sensor3.getId());
 
        // Initialize empty readings collections
        sensorReadings.put(sensor1.getId(), Collections.synchronizedList(new ArrayList<>()));
        sensorReadings.put(sensor2.getId(), Collections.synchronizedList(new ArrayList<>()));
        sensorReadings.put(sensor3.getId(), Collections.synchronizedList(new ArrayList<>()));
    }
 
    /**
     * Clear all data (useful for testing)
     */
    public void clear() {
        rooms.clear();
        sensors.clear();
        sensorReadings.clear();
    }
}