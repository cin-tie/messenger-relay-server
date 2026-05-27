FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/messenger-relay-server-1.0.0.jar app.jar

EXPOSE 6000

CMD ["java", "-jar", "app.jar", "6000"]