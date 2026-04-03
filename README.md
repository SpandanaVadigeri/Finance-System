# Finance Data Processing and Access Control Backend

## Overview

This project is a backend system for managing financial records with role-based access control. It supports user management, financial transactions, and dashboard analytics.

The system is designed with clean architecture, proper data modeling, and separation of concerns.

---

## Architecture

Controller → Service → Repository → Database
DTOs are used between Controller and Service layers.

---

## Core Features

### 1. User and Role Management

* Create users
* Assign roles (VIEWER, ANALYST, ADMIN)
* Role-based access control implemented in service layer

### 2. Financial Records Management

* Create financial records
* View all records
* Each record contains:

  * amount
  * type (INCOME / EXPENSE)
  * category
  * date

### 3. Dashboard APIs

* Total income
* Total expenses
* Net balance
* Category-wise totals

### 4. Access Control

* ADMIN → can create records
* VIEWER → read-only access
* ANALYST → read + analytics

---

##  API Endpoints

### User APIs

* POST /users
* GET /users/{id}

### Financial Record APIs

* POST /records
* GET /records

### Dashboard APIs

* GET /dashboard/summary
* GET /dashboard/category

---

##  Database Design

### User

* id, name, email, password, role_id, status

### Role

* id, name

### FinancialRecord

* id, user_id, amount, type, category, record_date

---

##  Technologies Used

* Java
* Spring Boot
* Spring Data JPA
* REST APIs

---

## Design Decisions

* Role is implemented as a separate entity for scalability
* DTOs are used to separate API layer from database layer
* Dashboard data is computed dynamically instead of being stored
* Minimal and clean design to ensure maintainability

---

## Conclusion

This system demonstrates backend design principles including data modeling, API structuring, role-based access control, and data processing logic.