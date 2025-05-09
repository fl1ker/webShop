FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY onlineShop/target/*.jar app.jar
COPY data ./data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
