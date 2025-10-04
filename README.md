# Spring Dogs Application

A full-stack application for managing dog records with authentication and authorization. This project demonstrates Spring Boot backend with React frontend integration.

## 🏗️ Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0 with Java 17
- **Database**: H2 in-memory database
- **Security**: JWT-based authentication with Spring Security
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Gradle

### automatiser
- **Framework**: React 18
- **UI Library**: Material-UI (MUI)
- **State Management**: Context API
- **HTTP Client**: Axios
- **Routing**: React Router v6

## 🚀 Features

- **User Management**: Login and role-based access (ADMIN/GUEST)
- **Dog Records**: Comprehensive dog information management
- **CRUD Operations**: Create, read, update, delete dogs (Admin only)
- **Search & Pagination**: Find dogs by various criteria
- **Responsive Design**: Works on desktop and mobile devices
- **Authentication**: JWT token-based security

## 📋 Dog Fields

The dog entity includes typical fields:
- Name, Breed, Age, Color, Weight
- Owner details (Name, Phone, Email)
- Birth Date, Medical Notes
- Status (Active, Inactive, Adopted, Deceased)
- Timestamps for audit trail

## 🛠️ Setup Instructions

### Backend Setup

1. **Prerequisites**:
   - Java 17 or higher
   - Gradle 7+

2. **Run Spring Boot Application**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access Points**:
   - API: http://localhost:8080/api
   - H2 Console: http://localhost:8080/h2-console
   - Database URL: `jdbc:h2:mem:testdb`
   - Username: `sa`, Password: `password`

### Frontend Setup

1. **Prerequisites**:
   - Node.js 16+ and npm

2. **Install Dependencies**:
   ```bash
   cd frontend
   npm install
   ```

3. **Start Development Server**:
   ```bash
   npm start
   ```

4. **Access**: http://localhost:3000

## 🔐 User Roles

### Admin Role
- Full CRUD operations on dogs
- View dog statistics
- Manage all dog records

### Guest Role
- View dogs (read-only)
- No create/edit/delete permissions
- Limited access to sensitive information

## 📁 Project Structure

```
spring-dogs/
├── src/main/java/com/example/springdogs/
│   ├── model/          # Entity classes (Dog, User)
│   ├── repository/     # JPA repositories
│   ├── service/        # Business logic
│   ├── controller/     # REST controllers
│   ├── dto/           # Data Transfer Objects
│   ├── config/        # Configuration classes
│   └── security/      # Security configuration
├── frontend/
│   ├── src/
│   │   ├── components/ # React components
│   │   ├── pages/      # Page components
│   │   ├── contexts/   # Context providers
│   │   ├── services/   # API services
│   │   └── App.js     # Main App component
├── build.gradle        # Gradle build configuration
└── README.md          # This file
```

## 🎯 API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout

### Dogs (Admin Only)
- `GET /api/dogs` - Get all dogs (paginated)
- `GET /api/dogs/{id}` - Get dog by ID
- `POST /api/dogs` - Create new dog
- `PUT /api/dogs/{id}` - Update dog
- `DELETE /api/dogs/{id}` - Delete dog
- `GET /api/dogs/stats` - Get dog statistics

### Dogs (Public Read)
- `GET /api/dogs` - View dogs (Guest access)
- `GET /api/dogs/{id}` - View dog details (Guest access)

## 📝 Learning Objectives

This project demonstrates:
- Spring Boot REST API development
- JPA/Hibernate entity mapping
- Spring Security with JWT
- Role-based authorization
- React modern hooks and context
- Material-UI component library
- Full-stack integration patterns

## 🔧 Development Tips

1. **Database**: H2 console is accessible during development for database inspection
2. **CORS**: Configured for localhost:3000 (React dev server)
3. **JWT**: Tokens expire after 24 hours (configurable)
4. **Validation**: Both frontend and backend validation
5. **Error Handling**: Comprehensive error responses

## 🚀 Production Considerations

For production deployment, consider:
- Replace H2 with PostgreSQL/MySQL
- Use environment-specific configurations
- Add comprehensive error logging
- Implement API rate limiting
- Add database migrations with Flyway
- Set up proper SSL certificates

## 🤝 Contributing

This is a learning project. Feel free to:
- Add new features
- Improve error handling
- Add unit tests
- Enhance the UI/UX
- Add data validation rules

