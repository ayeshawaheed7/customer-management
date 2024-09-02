FROM openjdk:17-jdk-slim
LABEL maintainer="ayeshawaheed135@gmail.com"
EXPOSE 9094
ADD target/*.jar kafka-topics.jar
ENTRYPOINT ["java", "-jar", "/kafka-topics.jar"]
