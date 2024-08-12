FROM openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9091
ADD target/*.jar fraud-service.jar
ENTRYPOINT ["java", "-jar", "/fraud-service.jar"]
