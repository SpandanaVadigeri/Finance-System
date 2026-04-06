#  Finance Management System - Role Based Financial Platform

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![JWT](https://img.shields.io/badge/JWT-Security-orange.svg)](https://jwt.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)


##  Project Overview

A comprehensive **Role-Based Financial Management System** that enables organizations to track income, expenses, and generate financial insights with granular access control. The system implements three distinct user roles with carefully defined permissions, JWT-based authentication, and real-time dashboard analytics.

###  Key Features

| Feature | Description |
|---------|-------------|
|  **JWT Authentication** | Secure token-based authentication with refresh token support |
|  **Role Management** | Three-tier role system: VIEWER, ANALYST, ADMIN |
|  **Transaction Management** | Complete CRUD operations for income/expense tracking |
|  **Dashboard Analytics** | Real-time summaries, trends, and category breakdowns |
|  **Soft Delete** | Data recovery capable deletion mechanism |
|  **Pagination & Filtering** | Efficient data retrieval for large datasets |
|  **Token Blacklisting** | Secure logout with immediate token invalidation |

---

##  Role-Based Access Control

### Role Capabilities Matrix

| Operation |  VIEWER |  ANALYST |  ADMIN |
|-----------|-----------|------------|----------|
| **View own transactions** | ✅ | ✅ | ✅ |
| **View all transactions** | ❌ | ✅ | ✅ |
| **View own dashboard** | ✅ | ✅ | ✅ |
| **View company dashboard** | ❌ | ✅ | ✅ |
| **Create transactions** | ❌ | ❌ | ✅ |
| **Update transactions** | ❌ | ❌ | ✅ |
| **Delete transactions** | ❌ | ❌ | ✅ |
| **View user list** | ❌ | ❌ | ✅ |
| **Create users** | ❌ | ❌ | ✅ |
| **Update user roles** | ❌ | ❌ | ✅ |
| **Activate/Deactivate users** | ❌ | ❌ | ✅ |

## Architecture Diagram
https://drive.google.com/file/d/1qKw6X7INYhj95NfGRDGKkvmtubgZ0e1P/view

##  Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | Spring Boot | 3.x |
| **Security** | Spring Security + JWT | 0.11.5 |
| **Database** | MySQL / H2 | 8.0 / latest |
| **ORM** | Spring Data JPA (Hibernate) | - |
| **Build Tool** | Maven | 3.8+ |
| **Java** | OpenJDK | 17 |
| **Documentation** | Swagger/OpenAPI  | 2.0+ |

---

## Installation and Setup

### Prerequisites

```bash
✓ Java 17 or higher
✓ Maven 3.8+
✓ MySQL 8.0 (or use H2 for testing)
✓ Git

```

### Step-by-Step Setup
## 1. Clone Repository
   - git clone https://github.com/SpandanaVadigeri/Finance-System.git
## 2. Configure Database

   Create MySQL database:
   CREATE DATABASE finance_db;
   CREATE USER 'finance_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON finance_db.* TO 'finance_user'@'localhost';
   FLUSH PRIVILEGES;


## 3. Configure application.properties

 src/main/resources/application.properties

## src/main/resources/application.properties

### Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=finance_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

### JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

### JWT Configuration (Change in production!)
jwt.secret=your-256-bit-secret-key-for-jwt-signing
jwt.expiration=3600000

### Logging
logging.level.com.financeProject=DEBUG
logging.level.org.springframework.security=DEBUG
## 4. Initialize Database with Roles
Run this SQL script to create initial roles:

sql

INSERT INTO roles (name) VALUES ('VIEWER');

INSERT INTO roles (name) VALUES ('ANALYST');

INSERT INTO roles (name) VALUES ('ADMIN');
## 5. Build and Run

### Build the project
mvn clean install

### Run the application
mvn spring-boot:run

### Or run the JAR
java -jar target/MyProject-0.0.1-SNAPSHOT.jar

## API Endpoints List 
1. POST /auth/login : User login with email and password
2. POST /auth/logout : Logout & blacklist JWT token

   ### ADMIN ONLY 
4. GET  /admin/users/all : Get all users
5. POST /admin/users/add : Create new user (viewer / analyst ) 
6. GET /admin/users{id}  : Get user by ID
7. PATCH	/admin/users/{id}/status?status=ACTIVE/INACTIVE	: Update user status
8. PATCH	/admin/users/{id}/role?role=VIEWER/ANALYST/ADMIN :	Update user role

   ### ADMIN + ANALYST
9.  GET	/records/all : Get all records (role-based)
10. GET	/records/{id}:	Get record by ID
11. POST	/records/add?userId={id} :	Create transaction
12. PUT	/records/{id} : Update transaction
13. DELETE	/records/{id} : Delete transaction
14. DELETE	/records/add :	Delete all transactions
15. GET	/records/filter?type=&category=&startDate=&endDate=	: Filter transactions
16. GET	/records/user/{userId} : Get records by user ID
17. GET  /records/paginated?page=0&size=10 : Paginated records

   ### ADMIN + ANALYST + VIEWER
18. GET	/dashboard : Role-based dashboard data
19. GET	/dashboard/summary : Personal income/expense summary
20. GET	/dashboard/category : Category-wise breakdown
21. GET	/dashboard/company-summary	: Company-wide summary
22. GET	/dashboard/trends	: Monthly trends
23. GET	/dashboard/category-analysis	: Category analysis
24. GET	/dashboard/recent-activity	Recent 5 transactions

## Personal details
 
 Author: 
Spandana V

Email: spandanavadigeri@gmail.com

LinkedIn: www.linkedin.com/in/spandana-vadigeri

GitHub: github.com/SpandanaVadigeri

