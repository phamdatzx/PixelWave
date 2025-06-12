# PixelWave - Social Media Platform

PixelWave is a modern social media platform built with Spring Boot, featuring image sharing, user interactions, and content moderation capabilities.

## Features

- 🔐 **Authentication & Authorization**

  - JWT-based authentication
  - OAuth2 integration with Google
  - Role-based access control (USER, ADMIN)
  - Email verification

- 📸 **Image Management**

  - Image upload and processing
  - AWS S3 integration for image storage
  - Automatic image tagging with python FastAPI, ClipModel
  - Multiple image support for posts

- 👥 **User Features**

  - User profiles with avatars
  - Friend system
  - User tagging in posts
  - Privacy settings for posts

- 📝 **Content Management**

  - Create, read, update, and delete posts
  - Comment system
  - Like system
  - Post reporting and moderation
  - User banning system

- 🔔 **Notifications**
  - Real-time notifications using WebSocket
  - Email notifications
  - Friend request notifications
  - Post interaction notifications

## Tech Stack

- **Backend Framework**: Spring Boot 3.4.3
- **Database**: PostgreSQL
- **Authentication**: Spring Security, JWT
- **Cloud Storage**: AWS S3
- **Real-time Communication**: WebSocket
- **Email Service**: Spring Mail
- **Image Processing**: Custom image processing service
- **API Documentation**: SpringDoc OpenAPI

## Prerequisites

- Java 23
- Maven
- PostgreSQL
- AWS Account (for S3)
- Google OAuth2 credentials

## Environment Variables

Create a `.env` file in the root directory with the following variables:

```env
# Database
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/pixelwave
JDBC_DATABASE_USERNAME=your_username
JDBC_DATABASE_PASSWORD=your_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret

# JWT
JWT_SECRET=your_jwt_secret
JWT_EXPIRY_TIME=3600
REFRESH_TOKEN_EXPIRY_TIME=604800

# Email
EMAIL_USERNAME=your_email
EMAIL_PASSWORD=your_email_password

# AWS
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_S3_BUCKET_NAME=your_bucket_name
AWS_REGION=your_region

# Client
CLIENT_URL=http://localhost:5173
```

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/yourusername/pixelwave.git
cd pixelwave
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Project Structure

```
src/main/java/com/pixelwave/spring_boot/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── DTO/            # Data Transfer Objects
├── exception/      # Custom exceptions
├── filter/         # Security filters
├── model/          # Entity classes
├── repository/     # Data access layer
├── service/        # Business logic
└── Application.java # Main application class
```
