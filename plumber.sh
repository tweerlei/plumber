#!/bin/sh

java --add-opens java.base/java.lang=ALL-UNNAMED -jar build/libs/plumber-1.0.0-SNAPSHOT.jar "$@"
