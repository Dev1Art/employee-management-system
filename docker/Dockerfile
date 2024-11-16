FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY ./src ./src
RUN unset MAVEN_CONFIG && ./mvnw clean package -DskipTests -X

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 libglu1-mesa xvfb xorg libgl1-mesa-glx \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app
RUN apt-get update && apt-get install -y unzip
RUN wget https://download2.gluonhq.com/openjfx/17.0.13/openjfx-17.0.13_linux-x64_bin-sdk.zip && \
    unzip openjfx-17.0.13_linux-x64_bin-sdk.zip && \
    mv javafx-sdk-17.0.13 /javafx-sdk && \
    rm openjfx-17.0.13_linux-x64_bin-sdk.zip

COPY --from=builder /opt/app/target/ems-1.0-SNAPSHOT.jar app.jar
RUN chmod +r /opt/app/app.jar
ENV DISPLAY=:99
ADD run.sh /run.sh
RUN chmod a+x /run.sh
ENTRYPOINT ["/run.sh"]