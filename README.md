# Distributed System Lab

This project demonstrates the core characteristics of a distributed system using two independent services that communicate over the network.

## Services

### Service A (Provider)
- **Port**: 8081
- **Endpoint**: `GET /api/data`
- **Description**: Exposes a simple HTTP API that responds to requests from other services.

### Service B (Consumer)
- **Port**: 8082
- **Endpoint**: `GET /api/consume`
- **Description**: Calls Service A over the network and handles failures gracefully when Service A is unavailable.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building the Project

```bash
mvn clean compile
```

## Running the Services

### Start Service A
```bash
mvn spring-boot:run -pl service-a
```

### Start Service B (in a separate terminal)
```bash
mvn spring-boot:run -pl service-b
```

## Testing

### Normal Operation
1. Start both services.
2. Call Service B: `curl http://localhost:8082/api/consume`
   - Should return: "Hello from Service A!"

### Failure Handling
1. Stop Service A.
2. Call Service B: `curl http://localhost:8082/api/consume`
   - Should return: "Service A is unavailable"

## Key Features

- **Independent Processes**: Services run as separate processes.
- **Network Communication**: Services communicate via HTTP over the network.
- **Failure Propagation**: Demonstrates how failures propagate across service boundaries.
- **Logging**: Basic logging to show request/response flow.
- **Graceful Degradation**: Service B handles Service A unavailability gracefully.

## Architecture

- Both services run on the same machine but on different ports.
- Service B makes HTTP calls to Service A.
- If Service A is down, Service B returns a fallback message instead of crashing.