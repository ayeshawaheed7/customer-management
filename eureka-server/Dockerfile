FROM eclipse-temurin:17.0.1_12-jdk
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 8761
ADD target/*.jar eureka-server.jar
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]
