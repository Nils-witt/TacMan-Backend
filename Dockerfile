
FROM eclipse-temurin:21-jre-alpine
ARG VERSION=0.0.0-SNAPSHOT
LABEL maintainer="Nils Witt <nils@nils-witt.de>" version="${VERSION}"


HEALTHCHECK --start-period=30s CMD wget -qO- http://localhost:8080/ || exit 1

ENV OVERLAYS_PATH='/overlays'
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]

