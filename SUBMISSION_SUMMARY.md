# CMPE 273 - Assignment 1 Summary

## Deliverables Checklist

✅ **GitHub Repository Link**: https://github.com/SreyaSomisetty17/273-Assignment-1

✅ **README Updates**: 
- How to run locally (complete build and run instructions)
- Success + failure proof (see TESTING_OUTPUT.md)
- "What makes this distributed?" explanation included

✅ **Working Distributed System**:
- Service A (Echo API) on localhost:8080
- Service B (Client) on localhost:8081
- HTTP communication between services
- Request logging with service name, endpoint, status, and latency
- Timeout handling (5 seconds)
- Failure demonstration (503 when Service A is down)

## Requirements Met

### Core Requirements ✅
1. ✅ Two independent processes (run in separate terminals)
2. ✅ HTTP communication
3. ✅ Basic request logging: service name, endpoint, status, latency
4. ✅ Service B uses timeout when calling Service A (5000ms)
5. ✅ Demonstrate failure: stop Service A; Service B returns 503 and logs error

### API Endpoints ✅
**Service A (Port 8080):**
- ✅ GET /health → {"status":"ok"}
- ✅ GET /echo?msg=hello → {"echo":"hello"}

**Service B (Port 8081):**
- ✅ GET /health → {"status":"ok"}
- ✅ GET /call-echo?msg=hello → calls Service A /echo and returns combined response

### Testing ✅
**Success Test:**
```bash
curl "http://localhost:8081/call-echo?msg=hello"
```
Response:
```json
{
  "service_b_message": "Successfully called Service A",
  "service_a_response": {"echo":"hello"},
  "total_latency_ms": 25
}
```

**Failure Test (Stop Service A):**
```bash
curl "http://localhost:8081/call-echo?msg=hello"
```
Response: HTTP 503
```json
{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: I/O error..."
}
```

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven 3.6+
- **HTTP Client**: RestTemplate with timeout configuration
- **Logging**: SLF4J with Logback

## Key Features Implemented

### 1. Comprehensive Logging
- Service name prefix: [Service-A] or [Service-B]
- Request tracking: endpoint, status, latency
- Error details with full stack trace information
- Thread-based request correlation

### 2. Timeout Protection
- Configurable timeout (5000ms default)
- Connection timeout and read timeout
- Prevents cascading delays

### 3. Graceful Failure Handling
- Returns HTTP 503 when downstream service unavailable
- Meaningful error messages
- Service B continues to operate when Service A is down
- No cascading failures

### 4. Observable System
- Latency tracking in milliseconds
- Clear log levels (INFO for success, ERROR for failures)
- Request/response logging
- Easy debugging with structured logs

## Code Structure

### Service A (Provider)
- `ServiceAApplication.java`: Spring Boot application entry point
- `ProviderController.java`: REST controller with /health and /echo endpoints

### Service B (Consumer)
- `ServiceBApplication.java`: Spring Boot application entry point
- `ConsumerController.java`: REST controller with /health and /call-echo endpoints
- `ConsumerService.java`: Business logic for calling Service A with timeout handling

### Configuration
- `application.properties`: Port, service URLs, timeout, logging configuration
- `pom.xml`: Maven dependencies and build configuration

## Documentation Provided

1. **README.md**: Complete setup and run instructions, architecture, what makes this distributed
2. **TESTING_OUTPUT.md**: Detailed test results with actual commands and responses
3. **DEBUGGING_GUIDE.md**: Answers to:
   - What happens on timeout?
   - What happens if Service A is down?
   - What do your logs show, and how would you debug?
4. **test-script.sh**: Automated testing script

## Understanding Questions Answered

### What happens on timeout?
Service B waits up to 5 seconds. If no response, throws exception, returns 503, logs error with latency. Service B continues handling other requests.

### What happens if Service A is down?
Connection immediately refused, Service B catches exception, returns 503 with clear error message. Service B remains operational (partial failure).

### What do your logs show, and how would you debug?
Logs show: service name, endpoint, status code, latency in ms, error details. Debug by checking health endpoints, tracing thread IDs, analyzing latency patterns, and looking for ERROR level logs.

## What Makes This Distributed?

1. **Independent Processes**: Two separate Java processes on different ports
2. **Network Communication**: HTTP communication over localhost (network stack)
3. **Partial Failure**: Service A can fail while Service B continues to operate
4. **Timeout Management**: Protection against slow/unresponsive services
5. **No Shared State**: Each service has separate memory, communicate only via network

## How to Submit

1. Ensure all code is pushed to GitHub: https://github.com/SreyaSomisetty17/273-Assignment-1
2. Submit the GitHub repository link to Canvas
3. Include screenshots or paste from TESTING_OUTPUT.md showing:
   - Success case with both services running
   - Failure case with Service A stopped and 503 response
   - Log output showing error details

## Future Enhancements (Optional)

- Add Docker containerization
- Implement circuit breaker pattern (Resilience4j)
- Add metrics and monitoring (Prometheus/Grafana)
- Implement distributed tracing (Zipkin/Jaeger)
- Add health check dependencies
- Implement retry logic with exponential backoff
- Add API documentation with Swagger/OpenAPI
