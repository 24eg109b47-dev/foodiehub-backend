#!/bin/sh
set -e

if [ -z "$1" ] || [ "$1" = "provided" ] || [ "$*" = "java provided" ]; then
    echo "Starting FoodieHub Spring Boot application on port $PORT..."
    exec java -Dserver.port="" -jar /app/app.jar
fi

exec "$@"
