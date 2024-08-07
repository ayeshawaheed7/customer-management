FROM openjdk:11-jre-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9091
ADD target/*.jar fraud-service.jar
ENTRYPOINT ["java", "-jar", "/fraud-service.jar"]
