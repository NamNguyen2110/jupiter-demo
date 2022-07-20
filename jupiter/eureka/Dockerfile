FROM adoptopenjdk/openjdk11:alpine-jre
VOLUME /tmp
EXPOSE 8762
ADD /target/eureka-0.0.1-SNAPSHOT.jar eureka.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/eureka.jar"]