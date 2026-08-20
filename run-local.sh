#!/bin/bash
set -e

echo "Starting thunai infrastructure..."
docker compose up -d postgres redis rabbitmq consul keycloak

echo "Waiting for services to be ready..."
sleep 5

echo "Starting Java services (requires Maven)..."
mvn clean package -DskipTests -pl thunai-common -am

java -jar thunai-gateway/target/thunai-gateway-*.jar &
GWPID=$!
java -jar thunai-tenant-service/target/thunai-tenant-service-*.jar &
T1PID=$!
java -jar thunai-order-service/target/thunai-order-service-*.jar &
T2PID=$!
java -jar thunai-customer-service/target/thunai-customer-service-*.jar &
T3PID=$!
java -jar thunai-notification-service/target/thunai-notification-service-*.jar &
T4PID=$!

echo "All services started."
echo "Frontend: cd thunai-frontend && npm install && npm run dev"
echo "API Gateway: http://localhost:8080"
echo "Keycloak: http://localhost:8085"
