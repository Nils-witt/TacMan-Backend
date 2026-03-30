#!/bin/bash
export VERSION=$(git rev-parse --short HEAD)
mvn clean package -DskipTests
DOCKER_TAG=$(git rev-parse --abbrev-ref HEAD)

docker buildx build --platform linux/amd64,linux/arm64 -t ghcr.io/nils-witt/tacman-backend:$DOCKER_TAG .
docker push ghcr.io/nils-witt/tacman-backend:$DOCKER_TAG

#docker buildx build --platform linux/amd64 -t registry.home.nils-witt.de/tacman-backend:dev .
#docker push registry.home.nils-witt.de/tacman-backend:dev