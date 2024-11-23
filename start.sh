#!/bin/bash

# Start VNC server
vncserver :1 -geometry 1280x720 -depth 24 -nolisten tcp -localhost

# Run the Java application with headless mode (for testing)
java -Djava.awt.headless=true --module-path /opt/javafx-sdk/javafx-sdk-17.0.13/lib --add-modules javafx.controls,javafx.fxml -jar app.jar