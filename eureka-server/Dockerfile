FROM --platform=linux/arm64 openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 8761
ADD target/*.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
