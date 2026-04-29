FROM openjdk:17-jdk-slim-bullseye 
WORKDIR /app  
COPY target/*.jar app.jar  
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]