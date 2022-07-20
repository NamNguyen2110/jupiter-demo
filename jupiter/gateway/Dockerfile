FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
EXPOSE 8762
ADD /target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gateway.jar"]