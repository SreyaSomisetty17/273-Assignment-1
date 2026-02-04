# Quick Reference Card

## Start Services

### Terminal 1 - Service A (Port 8080)
```bash
cd /Users/sreyasomisetty/CMPE-273/273-Assignment-1
mvn spring-boot:run -pl service-a
```

### Terminal 2 - Service B (Port 8081)
```bash
cd /Users/sreyasomisetty/CMPE-273/273-Assignment-1
mvn spring-boot:run -pl service-b
```

## Test Commands

### Success Tests (Both Services Running)
```bash
# Service A Health
curl http://localhost:8080/health

# Service A Echo
curl "http://localhost:8080/echo?msg=hello"

# Service B Health
curl http://localhost:8081/health

# Service B Calling Service A (main test)
curl "http://localhost:8081/call-echo?msg=hello"
```

### Failure Test
```bash
# Stop Service A (Ctrl+C in Terminal 1 or)
pkill -f "service-a"

# Call Service B - should return 503
curl -i "http://localhost:8081/call-echo?msg=hello"
```

## Expected Responses

### Success: /call-echo
```json
{
  "service_b_message": "Successfully called Service A",
  "service_a_response": {"echo":"hello"},
  "total_latency_ms": 25
}
```

### Failure: /call-echo (Service A down)
```
HTTP/1.1 503
{
  "error": "Service A is unavailable",
  "message": "Failed to connect to Service A: ..."
}
```

## Port Configuration

| Service | Port | Role |
|---------|------|------|
| Service A | 8080 | Echo API (Provider) |
| Service B | 8081 | Client (Consumer) |

## Files Overview

| File | Purpose |
|------|---------|
| README.md | Setup instructions, architecture, distributed explanation |
| TESTING_OUTPUT.md | Test results with screenshots/output |
| DEBUGGING_GUIDE.md | Troubleshooting and debugging guide |
| SUBMISSION_SUMMARY.md | Complete deliverables checklist |
| test-script.sh | Automated testing script |

## Common Commands

```bash
# Build project
mvn clean compile

# Stop all services
pkill -f "service-a"
pkill -f "service-b"

# View logs (if running in background)
tail -f /tmp/service-a.log
tail -f /tmp/service-b.log

# Run automated test
./test-script.sh
```

## Key Configuration

**Service B timeout**: 5000ms (5 seconds)
- Location: `service-b/src/main/resources/application.properties`
- Property: `service.a.timeout=5000`

**Service A URL**: http://localhost:8080
- Location: `service-b/src/main/resources/application.properties`
- Property: `service.a.url=http://localhost:8080`

## Distributed System Features

✓ Independent processes
✓ Network communication (HTTP)
✓ Timeout protection (5s)
✓ Partial failure handling
✓ Comprehensive logging (service, endpoint, status, latency)
✓ Graceful degradation (503 errors)

## GitHub Repository

https://github.com/SreyaSomisetty17/273-Assignment-1
