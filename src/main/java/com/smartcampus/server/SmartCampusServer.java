package com.smartcampus.server;
 
import com.smartcampus.config.SmartCampusApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
 
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * Embedded server for the Smart Campus API.
 * Uses Grizzly as the HTTP container and Jersey as the JAX-RS implementation.
 */
public class SmartCampusServer {
    private static final Logger LOGGER = Logger.getLogger(SmartCampusServer.class.getName());
    private static final String BASE_URI = "http://localhost:8080/";
 
    public static void main(String[] args) {
        try {
            // Create and start the embedded server
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                    URI.create(BASE_URI),
                    new SmartCampusApplication()
            );
 
            LOGGER.log(Level.INFO, "========================================");
            LOGGER.log(Level.INFO, "Smart Campus API Server Started");
            LOGGER.log(Level.INFO, "========================================");
            LOGGER.log(Level.INFO, "Base URL: " + BASE_URI);
            LOGGER.log(Level.INFO, "API URL: " + BASE_URI + "api/v1");
            LOGGER.log(Level.INFO, "Discovery: " + BASE_URI + "api/v1/");
            LOGGER.log(Level.INFO, "========================================");
            LOGGER.log(Level.INFO, "Press Ctrl+C to stop the server");
            LOGGER.log(Level.INFO, "========================================");
 
            // Keep the server running
            System.in.read();
 
            // Shutdown the server
            server.shutdown();
            LOGGER.log(Level.INFO, "Server stopped");
 
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start server: " + e.getMessage(), e);
            System.exit(1);
        }
    }
}