FROM openjdk:11-jdk AS build

COPY *.kts gradlew* /build/
COPY gradle /build/gradle

WORKDIR /build
RUN ./gradlew --no-daemon dependencies >/dev/null

COPY src /build/src
RUN ./gradlew build --no-daemon


FROM openjdk:11-jdk AS runtime
 
COPY --from=build /build/build/libs/*.jar /app/

WORKDIR /tmp

ENTRYPOINT [ "java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app/plumber-1.0.0-SNAPSHOT.jar" ]
