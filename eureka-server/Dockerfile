FROM openjdk:11-jre-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 8761
ADD target/*.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
