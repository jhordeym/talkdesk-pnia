#!/bin/sh
./mvnw clean package -Pdocker -U -Dmaven.test.skip=true
docker-compose up -d --build
