FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY ./src ./src
RUN unset MAVEN_CONFIG && ./mvnw clean package -DskipTests

FROM azul/zulu-openjdk:17.0.13-jre
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip libgtk-3-0 libglu1-mesa tightvncserver xfonts-base \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /opt/app
COPY --from=builder /opt/app/target/ems-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /opt/app/src/main/resources /opt/app/resources
RUN chmod +r /opt/app/app.jar
RUN wget https://download2.gluonhq.com/openjfx/17.0.13/openjfx-17.0.13_linux-x64_bin-sdk.zip && \
    unzip openjfx-17.0.13_linux-x64_bin-sdk.zip -d /opt/javafx-sdk && \
    rm openjfx-17.0.13_linux-x64_bin-sdk.zip

# Create a VNC configuration file
COPY vnc.conf /home/user/.vnc/

# Set environment variables
ENV USER=user
ENV DISPLAY=:1
ENV XAUTHORITY=/home/user/.Xauthority

# Copy the startup script
COPY start.sh /opt/app/start.sh

# Make the startup script executable
RUN chmod +x /opt/app/start.sh

# Start VNC server and run the application using the startup script
CMD ["/bin/bash", "/opt/app/start.sh"]