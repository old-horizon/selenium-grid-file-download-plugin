# syntax = docker/dockerfile:1.0-experimental
FROM maven:3.8.1-jdk-8-slim AS builder
COPY . /src
WORKDIR /src
RUN --mount=type=cache,target=/root/.m2 cd hub && mvn clean package -B

FROM dosel/zalenium
COPY --from=builder /src/hub/hub-rest/target/*.jar /home/seluser/file-download-hub-plugin.jar
RUN sed -i 's/\(-cp \${ZALENIUM_ARTIFACT}\)/\1:\/home\/seluser\/file-download-hub-plugin.jar/; s/\(-role hub\)/\1 -servlet com.github.old_horizon.selenium.hub.Downloads/' /home/seluser/zalenium.sh
