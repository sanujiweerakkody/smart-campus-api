package com.smartcampus.filter;
 
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * Cross-cutting logging filter for all API requests and responses.
 * Logs HTTP method, URI for incoming requests, and status code for outgoing responses.
 * 
 * Using JAX-RS filters for logging is advantageous because:
 * 1. Centralizes logging logic in one place (single responsibility)
 * 2. Eliminates code duplication across multiple resource methods
 * 3. Applied automatically to all requests without manual intervention
 * 4. Easy to enable/disable or modify logging behavior globally
 * 5. Separates cross-cutting concerns from business logic
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    
    private static final String REQUEST_START_TIME = "REQUEST_START_TIME";
 
    /**
     * Log incoming request details
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Record request start time for duration calculation
        requestContext.setProperty(REQUEST_START_TIME, System.currentTimeMillis());
        
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getPath();
        
        LOGGER.log(Level.INFO, ">>> [REQUEST] Method: " + method + " | URI: " + uri);
    }
 
    /**
     * Log outgoing response details
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        int statusCode = responseContext.getStatus();
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getPath();
        
        // Calculate request duration
        long startTime = (long) requestContext.getProperty(REQUEST_START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        
        LOGGER.log(Level.INFO, "<<< [RESPONSE] Method: " + method + 
                " | URI: " + uri + 
                " | Status: " + statusCode + 
                " | Duration: " + duration + "ms");
    }
}