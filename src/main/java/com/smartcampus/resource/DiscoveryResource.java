package com.smartcampus.resource;
 
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
 
/**
 * Discovery Resource - Entry point for the Smart Campus API.
 * Provides API metadata and links to primary resource collections (HATEOAS).
 * 
 * Endpoint: GET /api/v1/
 */
@Path("/")
public class DiscoveryResource {
 
    /**
     * GET /api/v1/
     * Returns API metadata with links to resource collections
     * 
     * This demonstrates HATEOAS (Hypermedia As The Engine Of Application State)
     * principles, which provide clients with:
     * 1. Self-documenting API structure
     * 2. Navigation guidance without hardcoding URLs in clients
     * 3. Easier API evolution without breaking clients
     * 4. Better discoverability of available resources
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDiscovery() {
        JsonObject discovery = Json.createObjectBuilder()
                .add("apiName", "Smart Campus Sensor & Room Management API")
                .add("version", "1.0.0")
                .add("description", "RESTful API for managing rooms and sensors across the campus")
                .add("contact", Json.createObjectBuilder()
                        .add("name", "Campus Facilities Team")
                        .add("email", "facilities@smartcampus.edu")
                        .build())
                .add("baseUrl", "/api/v1")
                .add("timestamp", System.currentTimeMillis())
                .add("resources", Json.createObjectBuilder()
                        .add("rooms", Json.createObjectBuilder()
                                .add("collection", "/api/v1/rooms")
                                .add("description", "Manage campus rooms")
                                .add("methods", Json.createArrayBuilder()
                                        .add("GET - List all rooms")
                                        .add("POST - Create new room")
                                        .build())
                                .build())
                        .add("sensors", Json.createObjectBuilder()
                                .add("collection", "/api/v1/sensors")
                                .add("description", "Manage campus sensors")
                                .add("methods", Json.createArrayBuilder()
                                        .add("GET - List all sensors (with optional type filter)")
                                        .add("POST - Create new sensor")
                                        .build())
                                .build())
                        .add("readings", Json.createObjectBuilder()
                                .add("collection", "/api/v1/sensors/{sensorId}/readings")
                                .add("description", "Manage sensor reading history")
                                .add("methods", Json.createArrayBuilder()
                                        .add("GET - Get all readings for a sensor")
                                        .add("POST - Add new reading for a sensor")
                                        .build())
                                .build())
                        .build())
                .build();
 
        return discovery.toString();
    }
}