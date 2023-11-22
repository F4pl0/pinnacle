@echo off

REM Run all Maven sub-modules
start mvn spring-boot:run -pl auth-server
start mvn spring-boot:run -pl discovery-server
start mvn spring-boot:run -pl api-gateway
start mvn spring-boot:run -pl user-service
start mvn spring-boot:run -pl portfolio-service
start mvn spring-boot:run -pl data-aggregator-service

REM Navigate to Angular project directory and start it
cd pinnacle-frontend
start npm run start