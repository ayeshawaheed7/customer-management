FROM openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9092
ADD target/*.jar notification-service.jar
ENTRYPOINT ["java", "-jar", "/notification-service.jar"]