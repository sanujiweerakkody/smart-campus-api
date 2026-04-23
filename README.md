# Smart Campus Sensor & Room Management API

## Overview

This project is a RESTful API developed using JAX-RS (Jersey) for managing rooms and sensors within a Smart Campus environment. The system enables facilities managers and automated systems to interact with campus infrastructure efficiently.

The API follows core REST principles such as:

•	Stateless communication

•	Resource-based architecture

•	Proper HTTP methods and status codes

•	Structured error handling

•	HATEOAS-driven discovery

### Technology Stack

•	Java (JDK 8+)

•	JAX-RS (Jersey)

•	Maven

•	Embedded server (Grizzly / similar)

•	In-memory storage (HashMap, ArrayList)

No database is used as per coursework requirements.

## How to Run the Project

### Prerequisites

•	Java JDK installed

•	Maven installed (optional if using NetBeans)

### Steps

#### Method 1  (Run Using NetBeans)

##### 1. Open Project
   
Open NetBeans

File → Open Project

Select SmartCampusAPI

##### 2. Build Project
   
Right-click project

Click Clean and Build

##### 3. Run Project
   
Right-click project

Click Run

OR click ▶ Run button

#### Base URL
http://localhost:8080/api/v1

#### Method 2  (Run Using Maven) 

##### 1. Clone repository (ONLY if needed)
   
git clone https://github.com/sanujiweerakkody/smart-campus-api.git

cd smart-campus-api

##### 2. Build project
 
mvn clean install

##### 3. Run project (ONLY if configured)
   
mvn exec:java

#### Base URL

http://localhost:8080/api/v1

## Sample API Requests (curl)

#### Get all rooms

curl -X GET http://localhost:8080/api/v1/rooms

#### Create a room

curl -X POST http://localhost:8080/api/v1/rooms \

-H "Content-Type: application/json" \

-d '{"id":"LIB-301","name":"Library Room","capacity":50}'

#### Get a specific room

curl -X GET http://localhost:8080/api/v1/rooms/LIB-301

#### Create a sensor

curl -X POST http://localhost:8080/api/v1/sensors \

-H "Content-Type: application/json" \

-d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":25.5,"roomId":"LIB-301"}'

#### Filter sensors by type

curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

#### Add a sensor reading

curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \

-H "Content-Type: application/json" \

-d '{"id":"r1","timestamp":1710000000000,"value":26.2}'

## Client-Server Architectures Coursework – Report Answers

### Part 1: Service Architecture & Setup

1. JAX-RS Resource Lifecycle

In JAX-RS, resource classes are instantiated on a per-request basis by default. This means a new instance of the resource class is created for each incoming HTTP request.

This approach ensures thread safety at the resource level, as each request operates on an isolated instance. However, shared application data (such as rooms and sensors) must persist across requests and therefore cannot be stored within resource instances.

To address this, a singleton data store is used. Since this shared state is accessed concurrently by multiple requests, thread safety is ensured using thread-safe data structures such as ConcurrentHashMap.

This design aligns with the stateless nature of REST, where each request is independent and does not rely on server-side session state.

2. HATEOAS and Hypermedia

HATEOAS (Hypermedia as the Engine of Application State) enables clients to dynamically discover available API resources through links included in responses.

The Discovery endpoint provides metadata and links to primary resources such as rooms and sensors. This eliminates the need for clients to hardcode URLs.

This approach improves loose coupling, flexibility, and API evolvability, allowing changes to endpoints without breaking existing clients. It also enhances the self-descriptiveness of the API, which is a key characteristic of mature RESTful systems.


### Part 2: Room Management

1. Returning IDs vs Full Objects

Returning only resource IDs reduces response size and network bandwidth usage. However, it requires clients to make additional requests to retrieve full details.

Returning full resource objects increases payload size but eliminates the need for follow-up requests, simplifying client-side logic.

In this implementation, full room objects are returned to improve usability and reduce client-server round trips, which is particularly beneficial in high-latency environments.

2. DELETE Idempotency

The DELETE operation is idempotent.

Idempotency means that multiple identical requests result in the same final system state, even if the responses differ.

Example:

•	First request: Room is deleted → 204 No Content

•	Subsequent requests: Room does not exist → 404 Not Found

Although the response changes, the final state remains the same (the room is deleted), which satisfies idempotency.

Returning 404 for repeated requests is appropriate because it accurately reflects that the resource no longer exists.


### Part 3: Sensor Operations

1. @Consumes(MediaType.APPLICATION_JSON)

The @Consumes(MediaType.APPLICATION_JSON) annotation specifies that the endpoint accepts only JSON-formatted requests.

If a client sends data in a different format (e.g., text/plain or application/xml), the JAX-RS runtime cannot process it and returns an HTTP 415 Unsupported Media Type response.

This mechanism relies on message body readers, which convert JSON data into Java objects. It enforces a strict API contract and ensures consistent request handling.

2. Query Parameters vs Path Parameters

Query parameters (e.g., /sensors?type=CO2) are more appropriate for filtering collections.

They are optional, flexible, and allow multiple filters to be combined (e.g., ?type=CO2&status=ACTIVE). This makes the API more scalable and easier to extend.

In contrast, using path parameters for filtering (e.g., /sensors/type/CO2) treats the filter as a hierarchical resource, which is less flexible and harder to maintain.

Therefore, query parameters provide a cleaner and more RESTful approach for search and filtering operations.


### Part 4: Sub-Resource Design

1. Sub-Resource Locator Pattern

The Sub-Resource Locator pattern delegates request handling to dedicated resource classes based on dynamic paths.

This improves separation of concerns, as each class is responsible for a specific domain (e.g., sensors vs sensor readings). It avoids the creation of overly complex “God classes” that manage multiple responsibilities.

This approach enhances maintainability, readability, and scalability, especially in large APIs.

2. Historical Data Management

Sensor readings are implemented as a sub-resource collection associated with each sensor.

Each reading is stored as part of a historical dataset, enabling time-based tracking of sensor values.

When a new reading is added:

•	It is appended to the sensor’s reading history

•	The currentValue field of the parent sensor is updated

This ensures data consistency between real-time values and historical records, supporting both monitoring and analysis.


### Part 5: Error Handling & Logging

1. HTTP 422 vs 404

HTTP 422 (Unprocessable Entity) is used when the request is syntactically valid but contains semantically incorrect data.

For example, when creating a sensor with a non-existent roomId, the request structure is correct, but the referenced resource does not exist.

Using 422 clearly communicates that the issue lies in the request data, not the endpoint itself. This distinction improves API clarity and allows clients to handle errors more effectively.

2. Security Risks of Stack Traces

Exposing internal stack traces in API responses is a security risk.

Stack traces may reveal sensitive information such as:

•	Class and package names

•	Internal logic and method calls

•	File paths and system structure

Attackers can use this information to identify vulnerabilities and target specific components.

To prevent this, the API returns generic error messages to clients while logging detailed stack traces internally. This follows the principle of information hiding, which is essential for secure API design.

3. Logging with Filters

JAX-RS filters provide a centralized mechanism for handling cross-cutting concerns such as logging.

By implementing ContainerRequestFilter and ContainerResponseFilter, all incoming requests and outgoing responses can be logged consistently.

This approach avoids duplicating logging code in every resource method, improving maintainability and ensuring uniform logging behaviour across the API.

It also keeps business logic clean by separating it from infrastructure concerns.

4. Exception Mapping

Exception mappers convert Java exceptions into structured HTTP responses.

By implementing ExceptionMapper<T>, specific exceptions can be mapped to appropriate HTTP status codes and JSON responses.

This ensures consistent error handling across the API and prevents exposure of internal implementation details.

It also improves client experience by providing predictable and meaningful error responses.

5. Data Validation Strategy

Data validation is implemented at multiple layers to ensure robustness.

•	JSON parsing ensures correct request format

•	Field validation checks for missing or invalid inputs

•	Business logic validation enforces rules (e.g., unique IDs)

•	Relationship validation ensures referenced resources exist

•	State validation enforces constraints (e.g., sensor status)


This layered approach ensures that errors are detected early and handled appropriately, improving overall system reliability.

## Demonstration

https://drive.google.com/file/d/1lhI54DeP4yreFGsArxZr9NKA5-91fAK3/view?usp=sharing

## Author

Sanuji Weerakkody (w2120578_20231508)

Undergraduate – Computer Science

Client-Server Architectures Coursework
