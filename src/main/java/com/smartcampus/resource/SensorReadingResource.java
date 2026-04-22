package com.smartcampus.resource;
 
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.service.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class SensorReadingResource {
    private static final Logger LOGGER = Logger.getLogger(SensorReadingResource.class.getName());
    private final DataStore dataStore = DataStore.getInstance();
    private final String sensorId;
 
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
        LOGGER.log(Level.INFO, "SensorReadingResource created for sensor: " + sensorId);
    }
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        LOGGER.log(Level.INFO, "Fetching readings for sensor: " + sensorId);
        
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor not found")
                            .add("sensorId", sensorId)
                            .build())
                    .build();
        }
 
        List<SensorReading> readings = dataStore.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }
 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) throws SensorUnavailableException {
        LOGGER.log(Level.INFO, "Adding reading for sensor: " + sensorId);
        
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor not found")
                            .add("sensorId", sensorId)
                            .build())
                    .build();
        }
 
        if (!sensor.isAvailable()) {
            throw new SensorUnavailableException(
                    "Cannot add readings. Sensor is currently in " + sensor.getStatus() + " state.",
                    sensorId,
                    sensor.getStatus()
            );
        }
 
        if (reading == null || Double.isNaN(reading.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Reading value is required and must be a valid number")
                            .build())
                    .build();
        }
 
        sensor.setCurrentValue(reading.getValue());
        dataStore.updateSensor(sensorId, sensor);
 
        SensorReading created = dataStore.addReading(sensorId, reading);
 
        LOGGER.log(Level.INFO, "Reading added successfully. New value: " + reading.getValue());
 
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
 
    @GET
    @Path("/{readingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingById(@PathParam("readingId") String readingId) {
        LOGGER.log(Level.INFO, "Fetching reading " + readingId + " for sensor: " + sensorId);
        
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor not found")
                            .add("sensorId", sensorId)
                            .build())
                    .build();
        }
 
        SensorReading reading = dataStore.getReadingById(sensorId, readingId);
        if (reading == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Reading not found")
                            .add("sensorId", sensorId)
                            .add("readingId", readingId)
                            .build())
                    .build();
        }
 
        return Response.ok(reading).build();
    }
}