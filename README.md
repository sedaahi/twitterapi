# Twitter Clone API

A full-stack Twitter/X clone project built with Spring Boot and React.

This repository contains the backend REST API. The application includes JWT-based authentication, tweet, comment, like and retweet operations, user-based authorization and PostgreSQL persistence.

The React frontend is maintained in a separate `twitter-frontend` project.

## Features

- User registration and login
- JWT authentication with Spring Security
- Create, list, update and delete tweets
- Add, update and delete comments
- Like and dislike tweets
- Retweet and undo retweet
- User profile posts, replies and liked tweets
- Ownership-based authorization
- Bean Validation
- Global exception handling
- Swagger / OpenAPI documentation
- CORS configuration for React frontend integration
- Unit, controller and repository tests

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT
- Springdoc OpenAPI / Swagger
- JUnit 5
- Mockito
- Maven

## Architecture

The backend follows a layered architecture:

```text
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

- **Controller:** Handles HTTP requests and responses.
- **Service:** Contains business logic and authorization rules.
- **Repository:** Handles database operations using Spring Data JPA.
- **Entity:** Represents the database model.
- **DTO:** Defines request and response models.
- **Security:** Handles JWT authentication and protected endpoints.

## Database Design

The application uses five main entities:

- User
- Tweet
- Comment
- Like
- Retweet

Main relationships:

- A user can create multiple tweets.
- A user can write multiple comments.
- A tweet can have multiple comments.
- Users can like multiple tweets.
- Users can retweet multiple tweets.
- Duplicate likes and retweets for the same user and tweet are prevented with unique constraints.

For the complete database diagram:

[View ER Diagram](docs/ER_DIAGRAM.md)

## API Endpoints

Except for registration, login and Swagger documentation, API endpoints require JWT authentication.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new user |
| POST | `/login` | Login and receive JWT |
| GET | `/tweet` | Get all tweets |
| POST | `/tweet` | Create a tweet |
| GET | `/tweet/findById?id={id}` | Get tweet by ID |
| GET | `/tweet/findByUserId?userId={id}` | Get tweets by user |
| PUT | `/tweet/{id}` | Update own tweet |
| DELETE | `/tweet/{id}` | Delete own tweet |
| POST | `/comment` | Add a comment |
| GET | `/comment/tweet/{tweetId}` | Get comments of a tweet |
| GET | `/comment/user/{userId}` | Get comments by user |
| PUT | `/comment/{id}` | Update own comment |
| DELETE | `/comment/{id}` | Delete an authorized comment |
| POST | `/like` | Like a tweet |
| POST | `/dislike` | Remove a like |
| GET | `/like/user/{userId}` | Get user's liked tweets |
| POST | `/retweet` | Retweet a tweet |
| DELETE | `/retweet/{id}` | Undo retweet |

## Authentication

The application uses JWT-based authentication.

After a successful login, the API returns a JWT. Protected endpoints require the token in the request header:

```text
Authorization: Bearer <token>
```

Spring Security and a custom JWT authentication filter validate the token before allowing access to protected endpoints.

## Authorization Rules

Some operations can only be performed by the owner of the resource:

- Only the tweet owner can update or delete a tweet.
- Only the comment owner can update a comment.
- A comment can be deleted by either the comment owner or the owner of the related tweet.
- Users can only remove their own likes and retweets.

## CORS

The React frontend and Spring Boot backend run on different ports during development.

Frontend:

```text
http://localhost:3200
```

Backend:

```text
http://localhost:3000
```

CORS configuration allows the React application to communicate with the backend while keeping the allowed origin configurable through environment settings.

## Local Setup

### Requirements

- Java 21
- PostgreSQL
- Maven or Maven Wrapper

Create a PostgreSQL database:

```sql
CREATE DATABASE twitter;
```

Configure the required environment variables:

```text
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key
CORS_ALLOWED_ORIGIN=http://localhost:3200
```

Then run the application:

```bash
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:3000
```

## Swagger

Swagger UI can be used to explore and test the API.

```text
http://localhost:3000/swagger-ui/index.html
```

For protected endpoints, authorize Swagger with a valid JWT token.

## Testing

The project contains tests for controller, service and repository layers using JUnit 5, Mockito and Spring Boot testing utilities.

Run all tests with:

```bash
./mvnw test
```

## Security

- Passwords are hashed using BCrypt.
- JWT is used for stateless authentication.
- Database credentials and JWT secrets are provided through environment variables.
- Sensitive credentials should not be committed to Git.

## Frontend

A React frontend was developed separately to consume this API.

The frontend includes:

- Authentication
- Home feed
- Tweet creation
- Tweet detail
- Tweet editing and deletion
- Comments
- Likes
- Retweets
- User profile with posts, replies and liked tweets

During development, the frontend runs on port `3200` and communicates with this backend on port `3000`.