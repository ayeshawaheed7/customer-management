FROM openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9090
ADD target/*.jar customer-service.jar
ENTRYPOINT ["java", "-jar", "/customer-service.jar"]
