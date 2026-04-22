package com.smartcampus.exception;
 
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
 
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    private static final Logger LOGGER = Logger.getLogger(SensorUnavailableExceptionMapper.class.getName());
 
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        LOGGER.log(Level.WARNING, "SensorUnavailable: " + exception.getMessage());
        
        String jsonResponse = Json.createObjectBuilder()
                .add("error", "FORBIDDEN")
                .add("status", 403)
                .add("message", exception.getMessage())
                .add("sensorId", exception.getSensorId() != null ? exception.getSensorId() : "unknown")
                .add("sensorStatus", exception.getStatus() != null ? exception.getStatus() : "unknown")
                .add("timestamp", System.currentTimeMillis())
                .build()
                .toString();
 
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(jsonResponse)
                .header("Content-Type", "application/json")
                .build();
    }
}