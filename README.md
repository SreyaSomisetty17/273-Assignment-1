# Distributed System Lab - CMPE 273

This project demonstrates a tiny, locally distributed system with two independent services that communicate over HTTP, include comprehensive logging, and demonstrate independent failure handling.

## Architecture

### Service A (Echo API) - Port 8080
- **GET /health** → `{"status":"ok"}`
- **GET /echo?msg=hello** → `{"echo":"hello"}`
- Role: Provider service that echoes back messages

### Service B (Client) - Port 8081
- **GET /health** → `{"status":"ok"}`
- **GET /call-echo?msg=hello** → Calls Service A `/echo` and returns combined response
- Role: Consumer service that calls Service A with timeout handling


## How to Run Locally

### Step 1: Build the Project
```bash
mvn clean compile
```

### Step 2: Start Service A (Terminal 1)
```bash
mvn spring-boot:run -pl service-a
```
Wait for the message: "Started ServiceAApplication in X seconds"

### Step 3: Start Service B (Terminal 2)
```bash
mvn spring-boot:run -pl service-b
```
Wait for the message: "Started ServiceBApplication in X seconds"

## Testing

### Success Test (Both Services Running)

Test Service A health:
```bash
curl http://localhost:8080/health
```
Expected: `{"status":"ok"}`

Test Service A echo:
```bash
curl "http://localhost:8080/echo?msg=hello"
```
Expected: `{"echo":"hello"}`

Test Service B health:
```bash
curl http://localhost:8081/health
```
Expected: `{"status":"ok"}`

Test Service B calling Service A:
```bash
curl "http://localhost:8081/call-echo?msg=hello"
```
Expected: 
```json
{
  "service_a_response": {"echo":"hello"},
  "service_b_message": "Successfully called Service A",
  "total_latency_ms": 45
}
```

### Failure Test (Service A Down)

1. Stop Service A (Ctrl+C in Terminal 1)
2. Call Service B:
```bash
curl "http://localhost:8081/call-echo?msg=hello"
```
Expected HTTP 503 response:
```json
{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: ..."
}
```

Check logs in Terminal 2 - you should see error logs like:
```
[Service-B] Failed to call Service A after XXms: I/O error on GET request...
[Service-B] Endpoint: /call-echo, Status: 503, Latency: XXms, Error: ...
```

## Logging Features

Both services log:
- **Service Name**: [Service-A] or [Service-B]
- **Endpoint**: The API endpoint being called
- **Status**: HTTP status code (200, 503, etc.)
- **Latency**: Time taken in milliseconds
- **Error Details**: When failures occur

## Success + Failure Proof

### ✅ Success Test Output (Both Services Running)
```bash
$ curl "http://localhost:8081/call-echo?msg=hello"
{
  "service_b_message": "Successfully called Service A",
  "service_a_response": {"echo":"hello"},
  "total_latency_ms": 25
}
```
<img width="426" height="126" alt="image" src="https://github.com/user-attachments/assets/914232e0-b338-45ca-aa03-238da2c2faf2" />

**Service B Logs:**
```
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  [Service-B] Successfully received response from Service A, Latency: 25ms
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  [Service-B] Endpoint: /call-echo, Status: 200, Latency: 25ms
```
<img width="907" height="62" alt="image" src="https://github.com/user-attachments/assets/262ae7f9-3e80-433b-bc7c-b2507862cc28" />

### ❌ Failure Test Output (Service A Stopped)
```bash
$ curl -i "http://localhost:8081/call-echo?msg=hello"
HTTP/1.1 503 
Content-Type: application/json

{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: I/O error on GET request for \"http://localhost:8080/echo\": Connection refused"
}
```
<img width="892" height="152" alt="image" src="https://github.com/user-attachments/assets/a2e5cfe6-e8e0-4fce-9725-4ce80022c50b" />


**Service B Logs:**
```
2026-02-04 10:20:31.049 [http-nio-8081-exec-4] INFO  [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:31.050 [http-nio-8081-exec-4] INFO  [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR [Service-B] Failed to call Service A after 2ms: Connection refused
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR [Service-B] Endpoint: /call-echo, Status: 503, Latency: 3ms
```
<img width="894" height="59" alt="image" src="https://github.com/user-attachments/assets/50e38fcf-6dfd-4385-8afb-d6d317fe785e" />

## What Makes This Distributed?

This system is distributed because Service A and Service B run as independent processes that communicate only through network messages over HTTP, experiencing real network latency and the possibility of communication failures. Each service can fail independently—when Service A goes down, Service B continues operating and gracefully handles the failure by returning a 503 error rather than crashing. The services do not share memory or state; all coordination happens via the network, with Service B implementing a 5-second timeout to protect against slow or unresponsive calls. This architecture mirrors real-world microservices that run in separate containers or on different machines, demonstrating fundamental distributed system properties: independent failure domains, network-based communication, partial failures, and timeout protection.

## Additional Documentation

- **[TESTING_OUTPUT.md](TESTING_OUTPUT.md)**: Detailed test results showing success and failure scenarios with actual curl commands and responses
- **[DEBUGGING_GUIDE.md](DEBUGGING_GUIDE.md)**: Comprehensive guide answering:
  - What happens on timeout?
  - What happens if Service A is down?
  - What do your logs show, and how would you debug?
- **[test-script.sh](test-script.sh)**: Automated test script for quick validation

