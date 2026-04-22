package com.smartcampus.exception;
 
public class LinkedResourceNotFoundException extends Exception {
    private String resourceType;
    private String resourceId;
 
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
 
    public LinkedResourceNotFoundException(String message, String resourceType, String resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
 
    public String getResourceType() {
        return resourceType;
    }
 
    public String getResourceId() {
        return resourceId;
    }
}
 