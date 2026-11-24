# LMS Portal

A full-stack Learning Management System built with **Spring Boot, React, and MongoDB**. The application provides role-based access for administrators and students, course management, learning materials, enrollment, and progress tracking.

## Features

### Authentication and Security
- Student registration and login
- JWT-based authentication
- BCrypt password hashing
- Role-based authorization for Admin and Student
- Protected REST API endpoints

### Admin Dashboard
- Create and manage courses
- Publish courses
- Add learning materials to courses
- View and delete learning materials
- Manage course content

### Student Dashboard
- Browse published courses
- Enroll in courses
- View enrolled courses
- Access course learning materials
- Mark lessons as completed
- Track course completion progress

## Technology Stack

| Layer | Technologies |
|---|---|
| Frontend | React, Vite, JavaScript, CSS |
| Backend | Java, Spring Boot, Spring Security |
| Database | MongoDB |
| Authentication | JWT, BCrypt |
| API | REST |
| Tools | VS Code, Maven, Git, GitHub |

## Project Structure

```text
lms-portal/
├── src/
│   └── main/
│       ├── java/com/lmsportal/
│       │   ├── controller/
│       │   ├── model/
│       │   ├── repository/
│       │   ├── security/
│       │   └── service/
│       └── resources/
│           └── application.properties
├── lms-frontend/
│   ├── public/
│   └── src/
│       ├── assets/
│       └── services/
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Prerequisites

Install the following before running the project:

- Java 21 or compatible newer JDK
- Node.js and npm
- MongoDB Community Server
- Git

## Backend Setup

Open a terminal in the project root.

### 1. Configure environment variables

PowerShell:

```powershell
$env:LMS_JWT_SECRET = "YOUR_LONG_RANDOM_SECRET_AT_LEAST_32_BYTES"
$env:MONGODB_URI = "mongodb://127.0.0.1:27017/lms_portal"
$env:LMS_ADMIN_EMAIL = "admin@lmsportal.local"
$env:LMS_ADMIN_PASSWORD = "YOUR_ADMIN_PASSWORD"
```

Use your own strong JWT secret and admin password. Do not commit these values to GitHub.

### 2. Start the backend

```powershell
.\mvnw.cmd spring-boot:run
```

The Spring Boot backend runs at:

`http://localhost:8080`

### 3. Run backend tests

```powershell
.\mvnw.cmd clean test
```

## Frontend Setup

Open a second terminal.

### 1. Navigate to the frontend

```powershell
cd lms-frontend
```

### 2. Install dependencies

```powershell
npm install
```

### 3. Start the frontend

```powershell
npm run dev
```

Open:

`http://localhost:5173`

### 4. Create a production build

```powershell
npm run build
```

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/login` | User login |
| POST | `/api/students` | Student registration |
| GET | `/api/courses` | View courses |
| POST | `/api/courses` | Create a course |
| GET | `/api/courses/{id}` | View course details |
| PUT | `/api/courses/{id}` | Update a course |
| DELETE | `/api/courses/{id}` | Delete a course |
| POST | `/api/materials` | Add learning material |
| GET | `/api/materials/course/{courseId}` | View course materials |
| DELETE | `/api/materials/{id}` | Delete learning material |
| POST | `/api/enrollments?courseId={id}` | Enroll in a course |
| GET | `/api/enrollments/me` | View my enrollments |
| DELETE | `/api/enrollments/course/{courseId}` | Cancel enrollment |
| POST | `/api/progress/complete?courseId={id}&materialId={id}` | Complete a lesson |
| GET | `/api/progress/me` | View my progress |

Protected endpoints require a valid JWT access token. Administrative and student permissions are enforced by the backend.

## Security Notes

- Never commit `.env` files, passwords, JWT secrets, or production database credentials.
- Configure secrets through environment variables.
- Use HTTPS and production-specific CORS settings when deploying.
- Use a secure MongoDB deployment and credentials for production.

## Future Improvements

- Course search and filtering
- File upload for learning materials
- Course certificates
- Email notifications
- Deployment to a cloud platform
- Automated backend and frontend tests

## Author

**B. Harini**

B.Tech – Computer Science and Engineering

## License

This project is developed for educational and portfolio purposes.
