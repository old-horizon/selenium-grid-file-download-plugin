# syntax = docker/dockerfile:1.0-experimental
FROM maven:3.8.1-jdk-8-slim AS builder
COPY . /src
WORKDIR /src
RUN --mount=type=cache,target=/root/.m2 mvn clean package -B

FROM elgalu/selenium
COPY --from=builder /src/node/node-rest/target/*.jar /home/seluser/file-download-node-plugin.jar
USER root
RUN sed -i 's/-jar \${SELENIUM_JAR_PATH}/-cp \/home\/seluser\/selenium.jar:\/home\/seluser\/file-download-node-plugin.jar org.openqa.grid.selenium.GridLauncherV3/; s/\(-role node\)/\1 -servlet com.github.old_horizon.selenium.node.Downloads/' /usr/bin/start-selenium-multinode.sh
USER seluser
RUN mkdir /home/seluser/Downloads
