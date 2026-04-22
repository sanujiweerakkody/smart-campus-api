package com.smartcampus.config;
 
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import com.smartcampus.resource.*;
import com.smartcampus.exception.*;
import com.smartcampus.filter.LoggingFilter;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.glassfish.jersey.jsonb.JsonBindingFeature; // 👈 ADD THIS

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    public SmartCampusApplication() {
        LOGGER.log(Level.INFO, "Initializing SmartCampusApplication...");

        // 🔥 ADD THIS LINE
        register(JsonBindingFeature.class);

        // Register Resource Classes
        LOGGER.log(Level.INFO, "Registering Resource Classes...");
        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);
        register(SensorReadingResource.class);

        LOGGER.log(Level.INFO, "Registering Exception Mappers...");

        // Register Exception Mappers
        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(GeneralExceptionMapper.class);

        LOGGER.log(Level.INFO, "Registering Filters...");

        // Register Filters
        register(LoggingFilter.class);

        LOGGER.log(Level.INFO, "SmartCampusApplication initialized successfully!");
    }
}
 