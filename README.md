# Spring Dogs 🐕

A full-stack web application for managing dog information with AI-powered safety predictions. Built with Spring Boot backend and React frontend, featuring JWT authentication and ChatGPT integration for dog safety analysis.

## 🌟 Features

### Core Functionality
- **Dog Management**: View, add, edit, and delete dog profiles
- **Search & Filter**: Find dogs by name, breed, or safety prediction
- **AI Safety Predictions**: ChatGPT-powered analysis of dog petting safety
- **Role-based Access**: Admin vs Guest user permissions
- **Responsive Design**: Modern UI built with Material-UI

### Authentication & Security
- **JWT Authentication**: Secure token-based authentication
- **Role-based Authorization**: Admin and Guest user roles
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Cross-origin resource sharing setup

### AI Integration
- **ChatGPT Safety Analysis**: AI-powered dog safety predictions
- **Caching**: Intelligent caching to reduce API calls
- **Error Handling**: Graceful handling of API failures and rate limits

## 🏗️ Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0 with Java 17
- **Database**: H2 in-memory database with JPA/Hibernate
- **Security**: Spring Security with JWT
- **AI Integration**: OpenAI ChatGPT API via WebClient
- **Caching**: Redis for performance optimization

### Frontend (React)
- **Framework**: React 18 with Material-UI
- **State Management**: Context API
- **HTTP Client**: Axios with React Query
- **Routing**: React Router DOM
- **Forms**: React Hook Form

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Node.js 16 or higher
- Redis (optional, for caching)
- OpenAI API key

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-dogs
   ```

2. **Configure environment variables**
   
   Create a `.env` file or set environment variables:
   ```bash
   export OPENAI_API_KEY=sk-your-actual-openai-api-key-here
   ```

   Or update `src/main/resources/application.yml`:
   ```yaml
   app:
     openaiApiKey: sk-your-actual-openai-api-key-here
   ```

3. **Run the Spring Boot application**
   ```bash
   ./gradlew bootRun
   ```

   The backend will be available at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

   The frontend will be available at `http://localhost:3000`

### Database Access

The application uses H2 in-memory database. Access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration (if implemented)

### Dogs Management
- `GET /api/dogs` - Get all dogs (with pagination, search, and filtering)
- `GET /api/dogs/{id}` - Get dog by ID
- `POST /api/dogs` - Create new dog (Admin only)
- `PUT /api/dogs/{id}` - Update dog (Admin only)
- `DELETE /api/dogs/{id}` - Delete dog (Admin only)

### Query Parameters
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `search` - Search by name, breed, or owner
- `prediction` - Filter by safety prediction (Yes, No, Cautiously, Error)

## 🔐 User Roles

### Guest User
- View all dogs
- Search and filter dogs
- View dog details
- Access safety predictions

### Admin User
- All guest permissions
- Create new dogs
- Edit existing dogs
- Delete dogs
- Manage dog safety predictions

## 🤖 AI Safety Predictions

The application integrates with OpenAI's ChatGPT API to provide safety predictions for dogs based on their characteristics:

### Prediction Categories
- **Yes**: Clearly safe and friendly dogs
- **No**: Potentially dangerous or aggressive dogs
- **Cautiously**: Dogs requiring careful approach
- **Error**: Invalid or nonsensical data

### Features
- **Intelligent Caching**: Avoids duplicate API calls for identical dog data
- **Rate Limit Handling**: Graceful degradation when API limits are exceeded
- **Error Recovery**: Fallback mechanisms for API failures
- **Response Validation**: Ensures consistent prediction format

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security**
- **Spring Data JPA**
- **Spring WebFlux** (for ChatGPT API calls)
- **H2 Database**
- **Redis** (caching)
- **JWT (jjwt)**
- **Lombok**
- **Gradle**

### Frontend
- **React 18**
- **Material-UI (MUI)**
- **React Router DOM**
- **React Hook Form**
- **React Query**
- **Axios**
- **Context API**

### Development Tools
- **Spring Boot DevTools**
- **H2 Console**
- **Gradle Wrapper**
- **npm**

## 📁 Project Structure

```
spring-dogs/
├── src/main/java/com/example/springdogs/
│   ├── config/          # Configuration classes
│   ├── controller/       # REST controllers
│   ├── data/            # Data initialization
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # JPA entities
│   ├── repository/      # Data repositories
│   ├── security/        # Security configuration
│   └── service/         # Business logic
├── src/main/resources/
│   └── application.yml  # Application configuration
├── frontend/
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── contexts/    # React contexts
│   │   ├── pages/       # Page components
│   │   └── services/    # API services
│   └── package.json
├── build.gradle         # Gradle build configuration
└── README.md
```

## 🧪 Testing

### Backend Tests
```bash
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
   ```bash
   ./gradlew build
   ```

2. Run the JAR:
   ```bash
   java -jar build/libs/spring-dogs-0.0.1-SNAPSHOT.jar
   ```

### Frontend Deployment
1. Build the production bundle:
   ```bash
   cd frontend
   npm run build
   ```

2. Serve the `build` directory with any static file server

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
app:
  jwtSecret: your-jwt-secret-key
  jwtExpirationMs: 86400000  # 24 hours
  openaiApiKey: sk-your-actual-openai-api-key-here

spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### Environment Variables
- `OPENAI_API_KEY`: Your OpenAI API key for ChatGPT integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the existing issues in the repository
2. Create a new issue with detailed information
3. Include error logs and steps to reproduce

## 🙏 Acknowledgments

- OpenAI for the ChatGPT API
- Spring Boot team for the excellent framework
- Material-UI for the beautiful React components
- The open-source community for various libraries and tools

---

**Happy coding! 🐕✨**
