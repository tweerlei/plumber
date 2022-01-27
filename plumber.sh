#!/bin/sh

java --add-opens java.base/java.lang=ALL-UNNAMED -jar build/libs/plumber-0.1.0-SNAPSHOT.jar "$@"
