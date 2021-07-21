FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} knowledge-tree.jar
ENTRYPOINT ["java", "-jar", "/knowledge-tree.jar"]