# Lab Inventory Management System

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-6DB33F?logo=springsecurity&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-4169E1?logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-000000?logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?logo=apachemaven&logoColor=white)

A secure, role-based REST API for managing laboratory inventory built with Spring Boot, featuring JWT authentication, real-time stock tracking, and comprehensive audit logging.

</div>

## Features
### 🔐 Authentication & Authorization
- JWT-based authentication
- Role-based access control (RBAC) with three distinct roles:
  - **SUPER_ADMIN** - full system access, user management
  - **LAB_ADMIN**: Lab management and stock operations
  - **STAFF**: Basic inventory viewing and limited operations
- Automatic super admin creation on first startup

### 📦 Inventory Management
- Item catalog with detailed categorization
- Supplier management with contact tracking
- Multi-lab support for different locations/departments
- Stock movement tracking with comprehensive history

### 📊 Stock Operations
- Stock tracking with quantity validation
- Stock movements: IN, OUT, ADJUST, TRANSFER
- Stock quantity threshold management

## 🏗️ Technology Stack

<div align="center">

| Layer | Technology | Version |
|-------|------------|---------|
| **Framework** | ![Spring Boot](https://img.shields.io/badge/-Spring%20Boot-6DB33F?logo=springboot&logoColor=white) | 3.5.9 |
| **Security** | ![Spring Security](https://img.shields.io/badge/-Spring%20Security-6DB33F?logo=springsecurity&logoColor=white) + ![JWT](https://img.shields.io/badge/-JWT-000000?logo=jsonwebtokens&logoColor=white) | 6.2.15 |
| **Database** | ![PostgreSQL](https://img.shields.io/badge/-PostgreSQL-4169E1?logo=postgresql&logoColor=white) | 14+ |
| **ORM** | ![Hibernate](https://img.shields.io/badge/-Hibernate-59666C?logo=hibernate&logoColor=white) | 6.6.39 |
| **Language** | ![Java](https://img.shields.io/badge/-Java%2017-007396?logo=java&logoColor=white) | 17+ |
| **Build Tool** | ![Maven](https://img.shields.io/badge/-Maven-C71A36?logo=apachemaven&logoColor=white) | 3.6+ |
| **Validation** | ![Bean Validation](https://img.shields.io/badge/-Bean%20Validation-007396?logo=java&logoColor=white) | 3.0 |

</div>

## Project Structure
```
src/main/java/com/kartoffan/labinventory/
├── api/                    # Global API handlers
├── config/                 # Configuration classes
├── controller/             # REST controllers
├── dto/                    # Data Transfer Objects
│   ├── auth/              # Authentication DTOs
│   ├── category/          # Category DTOs
│   ├── item/              # Item DTOs
│   ├── lab/               # Lab DTOs
│   ├── stock/             # Stock operation DTOs
│   ├── stockMovement/     # Movement history DTOs
│   ├── supplier/          # Supplier DTOs
│   └── user/              # User DTOs
├── exception/              # Custom exceptions
├── model/                  # JPA entities
├── repository/             # Data access layer
│   └── spec/              # JPA Specifications
├── security/               # Security configuration
│   ├── jwt/               # JWT components
│   └── role/              # Role/authority definitions
└── service/                # Business logic layer
    ├── auth/              # Authentication services
    ├── category/          # Category services
    ├── item/              # Item services
    ├── lab/               # Lab services
    ├── stock/             # Stock services
    ├── stockMovement/     # Movement services
    ├── supplier/          # Supplier services
    └── user/              # User services
```

## Database Schema
### Core Entities
- **User** - system users with roles and authentication
- **Item** - inventory items with categorization
- **Category** - item classification hierarchy
- **Supplier** - vendor/company information
- **Lab** - physical location/department
- **StockMovement** - transaction history with audit trail

## Installation & Setup
### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Step 1: Clone the repository
```bash
git clone https://github.com/kartoff-an/lab-inventory.git
cd lab-inventory
```

### Step 2: Database Setup
```sql
CREATE DATABASE lab_inventory;
CREATE USER lab_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lab_inventory TO lab_user;
```

### Step 3: Configuration
Setup environment variables
```properties
SERVER_PORT=8080 (or whatever you want)
DB_URL=jdbc:postgresql://localhost:5432/lab_inventory
DB_USERNAME=lab_user
DB_PASSWORD=your_password
JWT_SECRET=your-256-bit-secret-key-here-must-be-32-chars
JWT_EXPIRATION=86400000
```

### Step 4: Build and Run
```bash
# Build the project
mvn clean package

#Run the application
mvn spring-boot:run
```

### Step 5: Access the API
- API Base URL: `http://localhost:8080/api/v1`
- Swagger documentation: `http:localhost:8080/swagger`

# :)))
