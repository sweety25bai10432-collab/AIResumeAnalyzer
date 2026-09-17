FROM maven:3.9.16-eclipse-temurin-17

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

COPY frontend ./frontend

EXPOSE 8080

CMD ["java", "-jar", "target/AIResumeAnalyzer-1.0.jar"]
