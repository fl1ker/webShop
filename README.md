# webShop

## Description
This is an online shop application developed using **Spring Boot** and **MySQL**.

## How to Run the Application

### 1. Database Setup:
1. Download and install **MAMP** from the official website: [https://www.mamp.info](https://www.mamp.info).
2. After installation, open **MAMP** and click **Start Servers**. This will start the local server, including **MySQL**.
3. Click on **Open WebStart Page** and find the connection parameters:
   - **MySQL Port**
   - **User**
   - **Password**
4. Open the **`application.properties`** file in your project and add the following data:
   ```properties
    spring.jpa.hibernate.ddl-auto=update
    spring.datasource.url=jdbc:mysql://localhost:3306/onlineshop
    spring.datasource.username=root
    spring.datasource.password=root
    spring.jpa.show-sql=true
5. Go to phpMyAdmin through the WebStart Page in MAMP and create a new database named onlineshop
   (or any other name you used in application.properties).
### 2. Running:
1. Open the OnlineShopApplication.java file in your project and run.
2. Once the application is successfully started, open your browser and go to the following address: http://localhost:8080
