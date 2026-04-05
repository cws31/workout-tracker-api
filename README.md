Workout Tracker API

A robust and secure REST API built using Spring Boot that helps users track, manage, and analyze their workout activities efficiently. It also includes a powerful Admin Panel for system management and analytics.

Features
User Authentication (JWT-based)
User Registration & Login
User Features
Workout Management
Add Workout
Update Workout
Delete Workout
Mark Workout as Completed
Track Workout Progress
Admin Panel Features
Admin Dashboard
Total Users
Active Users
Total Workouts
Completed Workouts
Top Exercise Tracking
Daily Completed Workouts Analytics
User Management
Block / Unblock Users
Exercise Management
Add / Update / Delete Exercises
Workout Monitoring
View and manage all workouts
API Documentation using Swagger
Input Validation
Unit Testing with JUnit & Mockito
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
│── auth
│── common
│── config
│── admin
│── exercise
│── workout

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
GET /api/admin/dashboard
PATCH /api/admin/users/{id}/block
POST /api/admin/exercises
PUT /api/admin/exercises/{id}
DELETE /api/admin/exercises/{id}
GET /api/admin/workouts
How to Run
1️ Clone the Repository
git clone https://github.com/your-username/workout-tracker-api.git
2️ Navigate to Project
cd workout-tracker-api
3️ Run the Application
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
Future Improvements
Role-based Authorization (Admin/User enhancements)
Docker Support
Deployment (AWS / Render)
Pagination & Filtering
Advanced Workout Analytics Dashboard (Charts & Insights)
Author

Sonu Kumar
Email: gautamrocky909621@gmail.com

LinkedIn: https://www.linkedin.com/in/sonu-kumar-9a59b52a4/

⭐ If you like this project
Give it a ⭐ on GitHub and share it!