#!/bin/bash
docker buildx build --platform linux/amd64 -t ghcr.io/nils-witt/webmap_backend:dev .
docker push ghcr.io/nils-witt/webmap_backend:dev