FROM openjdk:17-jdk-alpine

ADD ./target/forum_alura_challenge-0.0.1-SNAPSHOT.jar api-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar","/api-app.jar"]