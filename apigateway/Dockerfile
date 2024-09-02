FROM openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9093
ADD target/*.jar apigateway.jar
ENTRYPOINT ["java", "-jar", "/apigateway.jar"]
