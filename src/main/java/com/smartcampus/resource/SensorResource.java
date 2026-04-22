package com.smartcampus.resource;
 
import com.smartcampus.model.Sensor;
import com.smartcampus.service.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * Sensor Resource - Manages sensor entities and their readings.
 * Implements filtering and delegates reading management to sub-resources.
 */
@Path("/sensors")
public class SensorResource {
    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());
    private final DataStore dataStore = DataStore.getInstance();
 
    /**
     * GET /api/v1/sensors
     * Retrieve all sensors, optionally filtered by type
     * 
     * Query Parameter vs Path Parameter Design:
     * - Query parameter (?type=CO2) is superior for filtering because:
     *   1. REST convention: query params for filtering/search, path params for resource identification
     *   2. Multiple filters possible: ?type=CO2&status=ACTIVE
     *   3. Optional filters don't pollute URL structure
     *   4. Easier for client libraries to build URLs
     *   5. Cache-friendly: same base resource, varied filters
     * - Path parameter (/sensors/type/CO2) would:
     *   1. Create hierarchical URL structure for non-hierarchical data
     *   2. Harder to support multiple filter combinations
     *   3. Pollutes URL namespace with filter-specific endpoints
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        LOGGER.log(Level.INFO, "Fetching all sensors" + (type != null ? " with type filter: " + type : ""));
        
        List<Sensor> sensors;
        if (type != null && !type.isEmpty()) {
            sensors = dataStore.getSensorsByType(type);
        } else {
            sensors = dataStore.getAllSensors();
        }
 
        return Response.ok(sensors).build();
    }
 
    /**
     * POST /api/v1/sensors
     * Create a new sensor
     * 
     * @Consumes(APPLICATION_JSON) enforces content-type checking:
     * - If client sends text/plain or application/xml, JAX-RS rejects with HTTP 415
     * - This ensures data integrity and prevents parsing errors
     * - Client must send: Content-Type: application/json
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) throws LinkedResourceNotFoundException {
        LOGGER.log(Level.INFO, "Creating new sensor: " + sensor.getId());
 
        // Validation: ID is required
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor ID is required")
                            .build())
                    .build();
        }
 
        // Dependency validation: roomId must exist
        if (sensor.getRoomId() == null || sensor.getRoomId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Room ID is required")
                            .build())
                    .build();
        }
 
        if (!dataStore.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Referenced room does not exist. Cannot create sensor without a valid room.",
                    "Room",
                    sensor.getRoomId()
            );
        }
 
        Sensor created = dataStore.createSensor(sensor);
        if (created == null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor with this ID already exists")
                            .build())
                    .build();
        }
 
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
 
    /**
     * GET /api/v1/sensors/{sensorId}
     * Retrieve details for a specific sensor
     */
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        LOGGER.log(Level.INFO, "Fetching sensor: " + sensorId);
        
        Sensor sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("error", "Sensor not found")
                            .add("sensorId", sensorId)
                            .build())
                    .build();
        }
 
        return Response.ok(sensor).build();
    }
 
    /**
     * Sub-Resource Locator Pattern
     * GET /api/v1/sensors/{sensorId}/readings
     * 
     * Delegates handling of reading operations to SensorReadingResource.
     * This pattern provides several architectural benefits:
     * 
     * 1. Separation of Concerns:
     *    - SensorResource: manages sensors collection
     *    - SensorReadingResource: manages readings for a specific sensor
     * 
     * 2. Scalability:
     *    - Avoids monolithic controller with dozens of methods
     *    - Easy to add new sub-resources without cluttering main resource
     * 
     * 3. Reusability:
     *    - SensorReadingResource can be extended for other entities
     * 
     * 4. Testability:
     *    - Each resource class is focused and easier to unit test
     * 
     * 5. Maintainability:
     *    - Related functionality grouped logically
     *    - Easier to locate and modify specific features
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsForSensor(@PathParam("sensorId") String sensorId) {
        LOGGER.log(Level.INFO, "Routing to SensorReadingResource for sensor: " + sensorId);
        
        // Return a new instance of the sub-resource
        // JAX-RS will handle injection of the sensorId parameter
        return new SensorReadingResource(sensorId);
    }
}
 