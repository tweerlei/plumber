FROM openjdk:11-jdk AS build

COPY *.kts gradlew* /build/
COPY gradle /build/gradle

WORKDIR /build
RUN ./gradlew --no-daemon dependencies >/dev/null

COPY src /build/src
RUN ./gradlew build --no-daemon


FROM openjdk:11-jre AS runtime

# Add the AWS RDS CA to the JVM trust store
COPY rds-combined-ca-bundle.pem /etc/ssl/certs/rds.crt
RUN echo y | keytool -importcert -alias rds -file /etc/ssl/certs/rds.crt -cacerts -keypass changeit -storepass changeit

COPY plumber /usr/local/bin/
COPY --from=build /build/build/libs/*.jar /usr/local/bin/build/libs/

WORKDIR /tmp

ENTRYPOINT [ "/usr/local/bin/plumber" ]
