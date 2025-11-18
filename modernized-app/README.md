# Turkish Airlines Flight Booking System - Modernized Version

## Overview

This is a modernized version of the Turkish Airlines Flight Booking System, rebuilt using modern Java technologies and best practices. The application has been transformed from a legacy Java Servlet/JSP application to a modern Spring Boot REST API with React frontend.

## Technology Stack

### Backend
- **Java 17** - Latest LTS version
- **Spring Boot 3.2.0** - Modern Java framework
- **Spring Security** - JWT-based authentication
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Modern relational database
- **Liquibase** - Database migration management
- **Maven** - Build and dependency management
- **OpenAPI/Swagger** - API documentation

### Frontend (To be implemented)
- **React 18** - Modern UI framework
- **TypeScript** - Type-safe JavaScript
- **Material-UI** - Modern component library
- **Axios** - HTTP client
- **React Router** - Client-side routing

### DevOps & Tools
- **Docker** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **JUnit 5** - Testing framework
- **Testcontainers** - Integration testing
- **Actuator** - Application monitoring

## Key Improvements

### 1. Modern Architecture
- **RESTful API** design replacing servlets
- **JWT Authentication** instead of session-based auth
- **Layered Architecture** with clear separation of concerns
- **Dependency Injection** using Spring IoC

### 2. Database Modernization
- **PostgreSQL** instead of MS Access
- **JPA Entities** with proper relationships
- **Database Migrations** using Liquibase
- **Connection Pooling** and transaction management

### 3. Security Enhancements
- **JWT Token-based** authentication
- **Role-based Authorization** using Spring Security
- **CORS Support** for cross-origin requests
- **Input Validation** using Bean Validation
- **SQL Injection Prevention** with JPA

### 4. API Design
- **RESTful Endpoints** following REST principles
- **Proper HTTP Status Codes** and error handling
- **Request/Response DTOs** for data transfer
- **OpenAPI Documentation** with Swagger UI

### 5. Testing & Quality
- **Unit Tests** with JUnit 5 and Mockito
- **Integration Tests** with Testcontainers
- **Code Coverage** tracking
- **Static Code Analysis** capabilities

## Project Structure

```
src/
├── main/
│   ├── java/com/turkishairlines/flightbooking/
│   │   ├── FlightBookingApplication.java          # Main application class
│   │   ├── config/                                # Configuration classes
│   │   │   └── SecurityConfig.java               # Security configuration
│   │   ├── controller/                           # REST controllers
│   │   │   └── FlightController.java            # Flight API endpoints
│   │   ├── dto/                                  # Data Transfer Objects
│   │   │   ├── FlightSearchRequest.java
│   │   │   └── FlightResponse.java
│   │   ├── entity/                               # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Flight.java
│   │   │   ├── Booking.java
│   │   │   ├── Seat.java
│   │   │   └── SeatClass.java
│   │   ├── repository/                           # Data repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── FlightRepository.java
│   │   │   ├── BookingRepository.java
│   │   │   └── SeatRepository.java
│   │   ├── security/                             # Security components
│   │   │   ├── JwtTokenUtil.java
│   │   │   ├── JwtRequestFilter.java
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   └── service/                              # Business logic
│   │       ├── UserService.java
│   │       ├── FlightService.java
│   │       ├── BookingService.java
│   │       └── SeatService.java
│   └── resources/
│       ├── application.properties                # Application configuration
│       └── db/changelog/                        # Database migrations
└── test/                                        # Test classes
```

## API Endpoints

### Public Endpoints
- `POST /api/flights/search` - Search flights
- `GET /api/flights/cities/departure` - Get departure cities
- `GET /api/flights/cities/arrival` - Get arrival cities

### Customer Endpoints (Requires CUSTOMER role)
- `GET /api/bookings` - Get user bookings
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}/confirm` - Confirm booking
- `DELETE /api/bookings/{id}` - Cancel booking

### Admin Endpoints (Requires ADMIN role)
- `POST /api/flights` - Create flight
- `PUT /api/flights/{id}` - Update flight
- `DELETE /api/flights/{id}` - Delete flight

### Manager Endpoints (Requires MANAGER role)
- `GET /api/flights` - View all flights
- `GET /api/bookings/all` - View all bookings

## User Roles

1. **ADMIN**
   - Manage flights (create, update, delete)
   - Set pricing and seat configurations
   - View system analytics

2. **MANAGER**
   - Approve flight changes
   - View bookings and reports
   - Manage customer issues

3. **CUSTOMER**
   - Search and book flights
   - View booking history
   - Manage personal bookings

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL 13 or higher
- Maven 3.8 or higher

### Database Setup
1. Create PostgreSQL database:
```sql
CREATE DATABASE flight_booking;
CREATE USER flight_user WITH PASSWORD 'flight_password';
GRANT ALL PRIVILEGES ON DATABASE flight_booking TO flight_user;
```

### Running the Application
1. Clone the repository
2. Navigate to the modernized-app directory
3. Run the application:
```bash
mvn spring-boot:run
```

### API Documentation
Once the application is running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

### Health Check
- **Health Endpoint**: http://localhost:8080/actuator/health

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

## Docker Support

### Build Docker Image
```bash
docker build -t turkish-airlines-booking .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## Migration from Legacy System

### Data Migration
1. Export data from MS Access database
2. Use migration scripts in `src/main/resources/db/migration/`
3. Run Liquibase migrations: `mvn liquibase:update`

### API Mapping
| Legacy Servlet | Modern API Endpoint |
|---|---|
| `/SearchFlights.do` | `POST /api/flights/search` |
| `/BookFlight.do` | `POST /api/bookings` |
| `/CurrentBooking.do` | `GET /api/bookings` |
| `/Login` | `POST /api/auth/login` |
| `/Logout` | `POST /api/auth/logout` |

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## Support

For questions or issues, please open a GitHub issue or contact the development team.