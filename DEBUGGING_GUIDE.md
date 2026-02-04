# Understanding the Distributed System - Key Questions Answered

## 1. What happens on timeout?

When Service B calls Service A, it has a configured timeout of **5000ms (5 seconds)** set in `application.properties`:

```properties
service.a.timeout=5000
```

**Scenario**: If Service A responds slowly or hangs:
- Service B will wait up to 5 seconds for a response
- If no response within 5 seconds, a `ResourceAccessException` is thrown
- Service B catches this exception and returns HTTP 503 with error message
- Service B logs the error with: service name, endpoint, status (503), latency, and error details
- **Important**: Service B does NOT hang indefinitely - it continues to handle other requests

**Code Implementation** (ConsumerService.java):
```java
this.restTemplate = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofMillis(timeout))  // Connection timeout
        .setReadTimeout(Duration.ofMillis(timeout))     // Read timeout
        .build();
```

---

## 2. What happens if Service A is down?

**When Service A is not running**:
1. Service B attempts to connect to `http://localhost:8080`
2. Connection is immediately refused (no service listening on port 8080)
3. RestTemplate throws `ResourceAccessException` with "Connection refused"
4. Service B catches the exception in `ConsumerService.callServiceAEcho()`
5. Service B returns HTTP 503 with a clear error message
6. Service B logs the failure with all details

**Key Points**:
- ✅ Service B continues to run normally
- ✅ Service B's own `/health` endpoint still returns 200 OK
- ✅ The system demonstrates **partial failure** - one service fails, the other remains operational
- ✅ No cascading failures or system crashes

**Example Response**:
```json
{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: I/O error on GET request..."
}
```

---

## 3. What do your logs show, and how would you debug?

### Log Format
Every log entry includes:
- **Service Name**: `[Service-A]` or `[Service-B]`
- **Endpoint**: The API path being accessed
- **Status**: HTTP status code or "Starting"
- **Latency**: Time taken in milliseconds
- **Error Details**: For failures, includes full error message

### Example Success Logs
```
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:19.173 [http-nio-8081-exec-2] INFO  [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  [Service-B] Successfully received response from Service A, Latency: 25ms
2026-02-04 10:20:19.198 [http-nio-8081-exec-2] INFO  [Service-B] Endpoint: /call-echo, Status: 200, Latency: 25ms
```

### Example Failure Logs
```
2026-02-04 10:20:31.049 [http-nio-8081-exec-4] INFO  [Service-B] Endpoint: /call-echo, Status: Starting, Message: hello
2026-02-04 10:20:31.050 [http-nio-8081-exec-4] INFO  [Service-B] Calling Service A at: http://localhost:8080/echo?msg=hello
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR [Service-B] Failed to call Service A after 2ms: I/O error on GET request for "http://localhost:8080/echo": Connection refused
2026-02-04 10:20:31.052 [http-nio-8081-exec-4] ERROR [Service-B] Endpoint: /call-echo, Status: 503, Latency: 3ms, Error: Failed to connect to Service A: ...
```

### Debugging Strategy

1. **Check if services are running**:
   ```bash
   curl http://localhost:8080/health  # Service A
   curl http://localhost:8081/health  # Service B
   ```

2. **Identify which service is failing**:
   - Look for ERROR level logs
   - Check HTTP status codes (503 indicates downstream service unavailable)
   - Look at latency - very low latency (2-3ms) with "Connection refused" = service is down
   - High latency near timeout (5000ms) = service is slow/hung

3. **Trace request flow**:
   - Each request has a thread ID (e.g., `[http-nio-8081-exec-4]`)
   - Follow that thread through the logs to see the complete request flow
   - Track latency at each step

4. **Common issues and how to identify them**:

   | Issue | Log Indicators | Solution |
   |-------|---------------|----------|
   | Service A down | "Connection refused", latency ~2-3ms | Start Service A |
   | Service A slow | Latency near 5000ms, timeout error | Investigate Service A performance |
   | Network issues | "Host unreachable" or DNS errors | Check network/firewall |
   | Wrong port | "Connection refused" on correct host | Verify port in application.properties |
   | Service A crashed | Was working, then "Connection refused" | Check Service A logs, restart |

5. **Monitoring in production**:
   - Set up alerts on 503 errors
   - Monitor average latency trends
   - Track error rates per endpoint
   - Use centralized logging (e.g., ELK stack) to correlate logs from both services

---

## Key Distributed Systems Concepts Demonstrated

### Partial Failure
- Service A can fail independently
- Service B continues to operate
- No shared fate between services

### Timeout Protection
- Prevents cascading delays
- Service B doesn't wait indefinitely
- System remains responsive

### Graceful Degradation
- Service B returns meaningful error (503)
- Error messages help with debugging
- System doesn't crash or hang

### Observable System
- Comprehensive logging
- Request tracing with thread IDs
- Latency tracking for performance monitoring
- Clear service boundaries in logs
