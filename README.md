## Finance Data Processing & Access Control Backend
This project is a backend system for a Finance Dashboard Application. It is designed to manage financial records, user roles, and provide summary analytics through well-structured APIs.

The system demonstrates backend development concepts such as:
- REST API design
- Data modeling
- Role-based access control
- Business logic implementation
- Validation and error handling

# Tech Stack
- Language: Java
- Framework: Spring Boot
- Database: MySQL
- ORM: Spring Data JPA (Hibernate)
- Build Tool: Maven
- Optional Enhancements: Spring Security, JWT
- Testing: Postman API 

# Features
User & Role Management
- Create and manage users
- Assign roles (ADMIN, ANALYST, VIEWER)
- Activate / deactivate users
- Role-based access restrictions

Financial Records Management
- Create financial records (income / expense)
- View all records
- Update existing records
- Delete records
- Filter records by:

        - Type (income/expense)
        - Category
        - Date range

Dashboard Summary APIs
- Total income
- Total expenses
- Net balance
- Category-wise totals
- Recent activity
- Monthly/weekly trends

Access Control:

Role-based permissions enforced at backend level:

Role	          Permissions

VIEWER	   -     Read-only access

ANALYST	   -     View records + analytics

ADMIN	   -     Full access (CRUD + user management)

Validation & Error Handling
- Input validation using annotations
- Proper HTTP status codes
- Global exception handling
- Protection against invalid operations
