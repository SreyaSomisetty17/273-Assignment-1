#!/bin/bash

echo "========================================="
echo "Testing Distributed System - Success Case"
echo "========================================="
echo ""

echo "1. Testing Service A Health:"
curl -s http://localhost:8080/health | python3 -m json.tool
echo ""

echo "2. Testing Service A Echo:"
curl -s "http://localhost:8080/echo?msg=hello" | python3 -m json.tool
echo ""

echo "3. Testing Service B Health:"
curl -s http://localhost:8081/health | python3 -m json.tool
echo ""

echo "4. Testing Service B calling Service A:"
curl -s "http://localhost:8081/call-echo?msg=hello" | python3 -m json.tool
echo ""

echo "========================================="
echo "Testing Distributed System - Failure Case"
echo "========================================="
echo ""
echo "Note: Please stop Service A (Ctrl+C in Terminal 1) before running this part"
echo ""
echo "Testing Service B when Service A is down:"
curl -i "http://localhost:8081/call-echo?msg=hello"
echo ""
