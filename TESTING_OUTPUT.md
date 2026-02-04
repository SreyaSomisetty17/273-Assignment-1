# Testing Output - Distributed System Lab

## Success Test (Both Services Running)

### 1. Service A Health Check
```bash
$ curl http://localhost:8080/health
```
**Response:**
```json
{"status":"ok"}
```

### 2. Service A Echo Endpoint
```bash
$ curl "http://localhost:8080/echo?msg=hello"
```
**Response:**
```json
{"echo":"hello"}
```

### 3. Service B Health Check
```bash
$ curl http://localhost:8081/health
```
**Response:**
```json
{"status":"ok"}
```

### 4. Service B Calling Service A
```bash
$ curl "http://localhost:8081/call-echo?msg=hello"
```
**Response:**
```json
{
    "service_b_message": "Successfully called Service A",
    "service_a_response": {
        "echo": "hello"
    },
    "total_latency_ms": 25
}
```

**Service B Logs:**
```
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  c.example.serviceb.ConsumerController - [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  c.example.serviceb.ConsumerService - [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  c.example.serviceb.ConsumerService - [Service-B] Successfully received response from Service A, Latency: 25ms
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  c.example.serviceb.ConsumerController - [Service-B] Endpoint: /call-echo, Status: 200, Latency: 25ms
```

---

## Failure Test (Service A Stopped)

### Stop Service A
```bash
$ pkill -f "service-a"
# or press Ctrl+C in the Service A terminal
```

### Call Service B When Service A is Down
```bash
$ curl -i "http://localhost:8081/call-echo?msg=hello"
```

**Response:**
```
HTTP/1.1 503 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 04 Feb 2026 18:20:31 GMT
Connection: close

{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: I/O error on GET request for \"http://localhost:8080/echo\": Connection refused"
}
```

**Service B Logs:**
```
2026-02-04 10:20:31.049 [http-nio-8081-exec-4] INFO  c.example.serviceb.ConsumerController - [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:31.050 [http-nio-8081-exec-4] INFO  c.example.serviceb.ConsumerService - [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR c.example.serviceb.ConsumerService - [Service-B] Failed to call Service A after 2ms: I/O error on GET request for "http://localhost:8080/echo": Connection refused
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR c.example.serviceb.ConsumerController - [Service-B] Endpoint: /call-echo, Status: 503, Latency: 3ms, Error: Failed to connect to Service A: I/O error on GET request for "http://localhost:8080/echo": Connection refused
```

---

## Key Observations

1. **Success Case**: 
   - Both services respond with HTTP 200
   - Service B successfully calls Service A and returns combined response
   - Total latency tracked: 25ms
   - Logs show service name, endpoint, status, and latency

2. **Failure Case**:
   - Service B returns HTTP 503 (Service Unavailable)
   - Clear error message indicating Service A is unavailable
   - Service B continues running (demonstrates partial failure)
   - Error logged with service name, endpoint, status 503, latency, and error details
   - Timeout protection (5 seconds) prevents hanging

3. **Logging Features**:
   - Each log entry includes: [Service-Name], Endpoint, Status, Latency
   - Error logs include detailed error messages
   - Consistent format across both services
