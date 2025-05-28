# webShop

## Description
This is an online shop application developed using **Spring Boot** with **Docker**.

## How to Run the Application
🛠 To build and run the application in a Docker container:
1. cd onlineShop
2. ./mvnw clean package
3. cd ..
```bash
docker-compose up --build
```
Once the container is running, the application will be available at:
🌍 Application: http://localhost:8080
🗂 H2 Console: http://localhost:8080/h2-console

⚙️ H2 Console Settings
When accessing the H2 console, use the following configuration:
JDBC URL: jdbc:h2:file:./data/shop
Driver Class: org.h2.Driver
Username: sa
Password: (leave empty)
