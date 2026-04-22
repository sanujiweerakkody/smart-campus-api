package com.smartcampus.exception;
 
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;
 
@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GeneralExceptionMapper.class.getName());
 
    @Override
    public Response toResponse(Throwable exception) {
        // Log the full exception for debugging
        LOGGER.log(Level.SEVERE, "========== UNEXPECTED ERROR ==========");
        LOGGER.log(Level.SEVERE, "Exception Type: " + exception.getClass().getName());
        LOGGER.log(Level.SEVERE, "Exception Message: " + exception.getMessage());
        LOGGER.log(Level.SEVERE, "Stack Trace:");
        
        // Print full stack trace to logs
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        LOGGER.log(Level.SEVERE, sw.toString());
        LOGGER.log(Level.SEVERE, "======================================");
        
        // Print to console as well for immediate visibility
        System.err.println("\n========== UNEXPECTED ERROR ==========");
        System.err.println("Exception Type: " + exception.getClass().getName());
        System.err.println("Exception Message: " + exception.getMessage());
        System.err.println("Full Stack Trace:");
        exception.printStackTrace();
        System.err.println("======================================\n");
        
        // Return JSON response with detailed error info
        String jsonResponse = Json.createObjectBuilder()
                .add("error", "INTERNAL_SERVER_ERROR")
                .add("status", 500)
                .add("exceptionType", exception.getClass().getName())
                .add("message", exception.getMessage() != null ? exception.getMessage() : "Unknown error")
                .add("timestamp", System.currentTimeMillis())
                .build()
                .toString();
 
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(jsonResponse)
                .header("Content-Type", "application/json")
                .build();
    }
}