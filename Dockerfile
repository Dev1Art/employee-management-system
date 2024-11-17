FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY ./src ./src
RUN unset MAVEN_CONFIG && ./mvnw clean package -DskipTests

FROM azul/zulu-openjdk:17.0.13-jre
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip libgtk-3-0 libglu1-mesa xvfb xorg libgl1-mesa-glx \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /opt/app
COPY --from=builder /opt/app/target/ems-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /opt/app/src/main/resources /opt/app/resources
RUN chmod +r /opt/app/app.jar
RUN wget https://download2.gluonhq.com/openjfx/17.0.13/openjfx-17.0.13_linux-x64_bin-sdk.zip && \
    unzip openjfx-17.0.13_linux-x64_bin-sdk.zip -d /opt/javafx-sdk && \
    rm openjfx-17.0.13_linux-x64_bin-sdk.zip
ENV DISPLAY=:99
CMD ["sh", "-c", "pkill -f Xvfb; rm -f /tmp/.X99-lock; Xvfb :99 -screen 0 1024x768x24 & sleep 2; java -Djava.awt.headless=true --module-path /opt/javafx-sdk/javafx-sdk-17.0.13/lib --add-modules javafx.controls,javafx.fxml -jar app.jar"]
