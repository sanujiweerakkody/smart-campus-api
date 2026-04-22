package com.smartcampus.exception;
 
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
 
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    private static final Logger LOGGER = Logger.getLogger(LinkedResourceNotFoundExceptionMapper.class.getName());
 
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        LOGGER.log(Level.WARNING, "LinkedResourceNotFound: " + exception.getMessage());
        
        String jsonResponse = Json.createObjectBuilder()
                .add("error", "UNPROCESSABLE_ENTITY")
                .add("status", 422)
                .add("message", exception.getMessage())
                .add("resourceType", exception.getResourceType() != null ? exception.getResourceType() : "unknown")
                .add("resourceId", exception.getResourceId() != null ? exception.getResourceId() : "unknown")
                .add("timestamp", System.currentTimeMillis())
                .build()
                .toString();
 
        return Response
                .status(422)
                .entity(jsonResponse)
                .header("Content-Type", "application/json")
                .build();
    }
}