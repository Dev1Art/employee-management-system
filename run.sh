#!/bin/bash
#rm /tmp/.X99-lock # Needed when the Docker container is restarted
Xvfb :99 -screen 0 1024x768x16 &
export DISPLAY=:99
java --module-path /javafx-sdk/lib -Djava.awt.headless=true --add-modules javafx.controls,javafx.fxml -jar /opt/app/app.jar
