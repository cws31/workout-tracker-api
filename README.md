Workout Tracker API

A robust and secure REST API built using Spring Boot that helps users track, manage, and analyze their workout activities efficiently. The system also includes a powerful Admin Panel for managing users, workouts, and exercises.

Features
User Features
User Authentication (JWT-based)
User Registration & Login
Workout Management
Add Workout
Update Workout
Delete Workout
Mark Workout as Completed
Track Workout Progress
Input Validation
Admin Panel Features
Admin Login (Secure Access)
Dashboard Analytics
Total Users
Active Users
Total Workouts
Completed Workouts
Top Exercises
Workouts Completed Per Day
User Management
Block / Unblock Users
Exercise Management
Add Exercise
Update Exercise
Delete Exercise
Workout Monitoring
View All User Workouts
Track Workout Status
Tech Stack
Java
Spring Boot
Spring Security
JWT Authentication
Spring Data JPA
Hibernate
MySQL / H2 Database
Swagger (OpenAPI 3)
JUnit & Mockito
Project Structure
com.workouttrackerapi
│── admin          # Admin panel (dashboard, user, exercise, workout management)
│── auth           # Authentication & authorization
│── common         # Common utilities & exceptions
│── config         # Security & configuration
│── exercise       # Exercise module
│── workout        # Workout module
Authentication

This API uses JWT (JSON Web Token) for securing endpoints.

Steps:
Register a user
Login to get JWT token
Use token in Swagger or Postman
API Documentation (Swagger)

After running the application, open:

http://localhost:8080/swagger-ui/index.html

Features:
View all endpoints
Test APIs directly
Use Authorize button for JWT
Sample Endpoints
Auth APIs
POST /api/auth/register
POST /api/auth/login
Workout APIs
POST /api/workouts
GET /api/workouts
PUT /api/workouts/{id}
DELETE /api/workouts/{id}
PATCH /api/workouts/{id}/complete
Admin APIs
Dashboard
GET /api/admin/dashboard
User Management
PATCH /api/admin/users/{id}/block
Exercise Management
POST /api/admin/exercises
PUT /api/admin/exercises/{id}
DELETE /api/admin/exercises/{id}
Workout Monitoring
GET /api/admin/workouts
How to Run
1 Clone the Repository
git clone https://github.com/your-username/workout-tracker-api.git
2 Navigate to Project
cd workout-tracker-api
3 Run the Application
mvn spring-boot:run
Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/workout_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
Testing

This project includes:

Unit Testing
Mockito-based Service Testing

Run tests:

mvn test
Best Practices Implemented
Layered Architecture (Controller → Service → Repository)
DTO Pattern
Global Exception Handling
Secure Password Encoding (BCrypt)
Clean Code Structure
Role-based Separation (Admin/User)
Future Improvements
Role-based Authorization Enhancements
Docker Support
Deployment (AWS / Render)
Pagination & Filtering
Advanced Workout Analytics Dashboard
Real-time Notifications
Author

Sonu Kumar
Email: gautamrocky909621@gmail.com

LinkedIn: https://www.linkedin.com/in/sonu-kumar-9a59b52a4/

If you like this project

Give it a star on GitHub and share it!