package com.smartcampus.resource;
 
import com.smartcampus.model.Room;
import com.smartcampus.service.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
@Path("/rooms")
public class RoomResource {
    private static final Logger LOGGER = Logger.getLogger(RoomResource.class.getName());
    private final DataStore dataStore = DataStore.getInstance();
 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        try {
            LOGGER.log(Level.INFO, "Fetching all rooms");
            List<Room> rooms = dataStore.getAllRooms();
            LOGGER.log(Level.INFO, "Found " + rooms.size() + " rooms");
            return Response.ok(rooms).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getAllRooms: " + e.getMessage(), e);
            return Response.status(500)
                    .entity(Json.createObjectBuilder()
                            .add("error", "INTERNAL_SERVER_ERROR")
                            .add("message", "Error fetching rooms: " + e.getMessage())
                            .build())
                    .build();
        }
    }
 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        try {
            LOGGER.log(Level.INFO, "===== CREATE ROOM REQUEST =====");
            LOGGER.log(Level.INFO, "Received room object: " + room);
            
            if (room == null) {
                LOGGER.log(Level.WARNING, "Room object is NULL");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Room object is null")
                                .build())
                        .build();
            }
 
            LOGGER.log(Level.INFO, "Room ID: " + room.getId());
            LOGGER.log(Level.INFO, "Room Name: " + room.getName());
            LOGGER.log(Level.INFO, "Room Capacity: " + room.getCapacity());
 
            if (room.getId() == null || room.getId().isEmpty()) {
                LOGGER.log(Level.WARNING, "Room ID is empty or null");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Room ID is required")
                                .build())
                        .build();
            }
 
            LOGGER.log(Level.INFO, "Creating room with ID: " + room.getId());
            Room created = dataStore.createRoom(room);
            
            if (created == null) {
                LOGGER.log(Level.WARNING, "Room already exists: " + room.getId());
                return Response.status(Response.Status.CONFLICT)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Room with this ID already exists")
                                .build())
                        .build();
            }
 
            LOGGER.log(Level.INFO, "Room created successfully: " + created);
            return Response.status(Response.Status.CREATED)
                    .entity(created)
                    .build();
                    
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERROR in createRoom: " + e.getClass().getName() + " - " + e.getMessage(), e);
            e.printStackTrace();
            
            return Response.status(500)
                    .entity(Json.createObjectBuilder()
                            .add("error", "INTERNAL_SERVER_ERROR")
                            .add("exception", e.getClass().getName())
                            .add("message", e.getMessage())
                            .build())
                    .build();
        }
    }
 
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        try {
            LOGGER.log(Level.INFO, "Fetching room: " + roomId);
            
            Room room = dataStore.getRoomById(roomId);
            if (room == null) {
                LOGGER.log(Level.INFO, "Room not found: " + roomId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Room not found")
                                .add("roomId", roomId)
                                .build())
                        .build();
            }
 
            return Response.ok(room).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getRoomById: " + e.getMessage(), e);
            return Response.status(500)
                    .entity(Json.createObjectBuilder()
                            .add("error", "INTERNAL_SERVER_ERROR")
                            .add("message", e.getMessage())
                            .build())
                    .build();
        }
    }
 
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) throws RoomNotEmptyException {
        try {
            LOGGER.log(Level.INFO, "Deleting room: " + roomId);
            
            Room room = dataStore.getRoomById(roomId);
            if (room == null) {
                LOGGER.log(Level.INFO, "Room not found for deletion: " + roomId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Room not found")
                                .add("roomId", roomId)
                                .build())
                        .build();
            }
 
            if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
                LOGGER.log(Level.WARNING, "Cannot delete room with sensors: " + roomId);
                throw new RoomNotEmptyException(
                        "Room has " + room.getSensorIds().size() + " active sensors",
                        roomId,
                        room.getSensorIds().size()
                );
            }
 
            boolean deleted = dataStore.deleteRoom(roomId);
            if (!deleted) {
                LOGGER.log(Level.WARNING, "Failed to delete room: " + roomId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Json.createObjectBuilder()
                                .add("error", "Failed to delete room")
                                .build())
                        .build();
            }
 
            LOGGER.log(Level.INFO, "Room deleted successfully: " + roomId);
            return Response.noContent().build();
        } catch (RoomNotEmptyException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in deleteRoom: " + e.getMessage(), e);
            return Response.status(500)
                    .entity(Json.createObjectBuilder()
                            .add("error", "INTERNAL_SERVER_ERROR")
                            .add("message", e.getMessage())
                            .build())
                    .build();
        }
    }
}
