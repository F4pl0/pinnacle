#!/bin/bash

# Run all Maven sub-modules
mvn spring-boot:run -pl auth-server &
mvn spring-boot:run -pl discovery-server &
mvn spring-boot:run -pl api-gateway &
mvn spring-boot:run -pl user-service &
mvn spring-boot:run -pl portfolio-service &
mvn spring-boot:run -pl data-aggregator-service &

# Navigate to Angular project directory and start it
cd pinnacle-frontend
npm run start &