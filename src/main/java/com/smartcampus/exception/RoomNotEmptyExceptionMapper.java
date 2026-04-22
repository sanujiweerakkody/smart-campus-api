package com.smartcampus.exception;
 
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
 
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    private static final Logger LOGGER = Logger.getLogger(RoomNotEmptyExceptionMapper.class.getName());
 
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        LOGGER.log(Level.WARNING, "RoomNotEmpty: " + exception.getMessage());
        
        String jsonResponse = Json.createObjectBuilder()
                .add("error", "CONFLICT")
                .add("status", 409)
                .add("message", exception.getMessage())
                .add("roomId", exception.getRoomId() != null ? exception.getRoomId() : "unknown")
                .add("activeSensors", exception.getSensorCount())
                .add("timestamp", System.currentTimeMillis())
                .build()
                .toString();
 
        return Response
                .status(Response.Status.CONFLICT)
                .entity(jsonResponse)
                .header("Content-Type", "application/json")
                .build();
    }
}
 