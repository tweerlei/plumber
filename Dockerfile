FROM openjdk:11-jdk AS build

ARG PACKAGE_VERSION=0.0.1-SNAPSHOT

COPY *.kts *.properties gradlew* /build/
COPY gradle /build/gradle

WORKDIR /build
RUN ./gradlew --no-daemon dependencies >/dev/null

COPY src /build/src
RUN ./gradlew --no-daemon -Pversion=${PACKAGE_VERSION} build


FROM openjdk:11-jre AS runtime

# Add the AWS RDS CA to the JVM trust store
COPY rds-combined-ca-bundle.pem /etc/ssl/certs/rds.crt
RUN echo y | keytool -importcert -alias rds -file /etc/ssl/certs/rds.crt -cacerts -keypass changeit -storepass changeit && \
	rm -f /usr/local/bin/docker-java-home

COPY plumber /usr/local/bin/
COPY --from=build /build/build/libs/*.jar /usr/local/bin/build/libs/

WORKDIR /tmp

ENTRYPOINT [ "/usr/local/bin/plumber" ]
