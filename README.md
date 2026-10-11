# Library Management System - Backend

Production-ready Java Spring Boot REST API for managing books, members, issues, and session-based authentication in the Library Management System.

## Technology Stack

- **Java**: 21
- **Framework**: Spring Boot 4.1.1 (Spring Security, Spring Data JPA)
- **Database**: MySQL 8 (Hibernate ORM)
- **Build Tool**: Apache Maven (`mvnw` / `mvnw.cmd`)
- **Authentication**: HTTP Session authentication (`JSESSIONID`) with BCrypt password hashing and `SameSite=None; Secure` cookies for cross-origin deployments.

## Project Structure

```
├── .mvn/wrapper/        # Maven wrapper properties
├── src/
│   ├── main/
│   │   ├── java/com/library/librarymanagement/
│   │   │   ├── config/      # SecurityConfig, SessionAuthFilter, SameSiteCookieFilter
│   │   │   ├── controller/  # AuthController, BookController, MemberController, BookIssueController
│   │   │   ├── dto/         # LoginRequest, RegisterRequest, IssueRequest, UserResponse
│   │   │   ├── entity/      # User, Book, Member, BookIssue
│   │   │   ├── repository/  # UserRepository, BookRepository, MemberRepository, BookIssueRepository
│   │   │   └── service/     # AuthService, BookService, MemberService, BookIssueService
│   │   └── resources/
│   │       └── application.properties
├── mvnw                 # Unix Maven wrapper
├── mvnw.cmd             # Windows Maven wrapper
└── pom.xml              # Maven dependencies and build definition
```

## Getting Started

### Prerequisites

- Java 21 JDK
- MySQL 8.0+

### 1. Database Configuration

Create the MySQL database:

```sql
CREATE DATABASE library_management;
```

### 2. Environment Variables

The backend supports standard production environment variables with local defaults:

| Variable | Description | Default (Local) |
| :--- | :--- | :--- |
| `PORT` | HTTP server port | `8081` |
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/library_management` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | *None* |
| `FRONTEND_URL` | Allowed frontend origin for CORS | `http://localhost:5173` |

### 3. Build & Package

Using Maven wrapper:

```bash
# Windows
mvnw.cmd clean package -DskipTests

# Linux/macOS
./mvnw clean package -DskipTests
```

### 4. Run Locally

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

The application runs on `http://localhost:8081`.

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Register a new student member account
- `POST /api/auth/login` - Authenticate and establish session
- `GET /api/auth/me` - Get current authenticated user profile
- `POST /api/auth/logout` - Invalidate session

### Books (`/api/books`)
- `GET /api/books` - List all catalog books
- `POST /api/books` - Add a new book (requires auth)
- `PUT /api/books/{id}` - Update book details (requires auth)
- `DELETE /api/books/{id}` - Remove a book (requires auth)
- `GET /api/books/count` - Count total catalog books (requires auth)

### Members (`/api/members`)
- `GET /api/members` - List all members (requires auth)
- `POST /api/members` - Register a new member (requires auth)
- `PUT /api/members/{id}` - Update member (requires auth)
- `DELETE /api/members/{id}` - Delete member (requires auth)
- `GET /api/members/count` - Count total members (requires auth)

### Issues & Returns (`/api/issues`)
- `GET /api/issues` - List all book issues (requires auth)
- `POST /api/issues` - Issue a book to a member (requires auth)
- `PUT /api/issues/{id}/return` - Process return of issued book (requires auth)
- `GET /api/issues/count` - Count total issues (requires auth)
- `GET /api/issues/member/{memberId}` - List issues by member (requires auth)
