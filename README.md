# Blog System Backend

A **Spring Boot RESTful backend** for a full-featured blogging platform — supporting **user authentication, blog management, comments, voting, bookmarks, followers, and notifications**.

Built with **clean architecture**, **DTO pattern**, **Redis caching**, and **JWT-based Spring Security**.

---

## Base URL (Local)

[http://localhost:8080](http://localhost:8080)

---

## 🧱 Project Architecture

The system follows a modular **service-oriented architecture** with clear separation of concerns between layers:

- **Entity Layer:**  
  Each major domain (User, Blog, Comment, Vote, Bookmark, Follower, Notification) has its own JPA entity class mapped to the corresponding database table using ORM (Hibernate/JPA).

- **Repository Layer:**  
  Every entity has a dedicated **JPA Repository interface** responsible only for direct data access related to that specific entity.  
  Repositories never call other repositories directly.

- **Service Layer:**  
  Each entity has its own **Service class** that contains the business logic.  
  Services interact with their own repositories and may communicate with other services if data from another entity is required (for example, when fetching a user’s blogs or sending notifications after a comment).

- **DTO Layer:**  
  To ensure clean and secure data flow, each entity has **Request and Response DTOs** (e.g., `BlogRequest`, `BlogResponse`).  
  Services convert entities to DTOs before sending them to the controllers, maintaining encapsulation and preventing exposure of internal data.

- **Controller Layer:**  
  REST Controllers handle HTTP requests and responses, delegating logic to their corresponding services.  
  They only work with DTOs, ensuring a consistent and secure API interface.

This layered design promotes maintainability, scalability, and testability while ensuring each module remains independent and reusable.

---

## Authentication

JWT Authentication is implemented using **Spring Security**.  
Authorization is role-based (e.g., `USER`, `ADMIN`).

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | Authenticate user and receive JWT token |

Include the JWT token in all protected requests:
```

Authorization: Bearer <your_token>

````

---

## 👤 Users

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/users` | Get all users |
| GET | `/users/{userId}` | Get a user by ID |
| PATCH | `/users` | Update user profile |
| DELETE | `/users/{userId}` | Delete a user |

---

## 📝 Blogs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/blogs` | Get all blogs |
| GET | `/blogs/user/{userId}` | Get blogs by user |
| POST | `/blogs/user/{userId}` | Create a new blog |
| GET | `/blogs/{blogId}` | Get blog by ID |
| PATCH | `/blogs/{blogId}` | Update blog |
| DELETE | `/blogs/{blogId}` | Delete blog |

---

## 💬 Comments

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/comments/blog/{blogId}?page={page}&size={size}` | Get paginated comments for a blog |
| POST | `/comments/{userId}/{blogId}` | Add new comment to blog |
| GET | `/comments/{commentId}` | Get comment by ID |
| PATCH | `/comments/{commentId}` | Edit comment |
| DELETE | `/comments/{commentId}` | Delete comment |

---

## 👍 Votes

### Blog Votes
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/votes/blog/{blogId}` | Get blog votes |
| POST | `/votes/blog/{userId}/{blogId}` | Add vote |
| PATCH | `/votes/blog/{userId}/{blogId}` | Update user vote |

### Comment Votes
| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/votes/comment/{commentId}` | Get comment votes |
| POST | `/votes/comment/{userId}/{commentId}` | Add vote |
| PATCH | `/votes/comment/{userId}/{commentId}` | Update user vote |

---

## 📑 Bookmarks

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/bookmarks/user/{userId}` | Get user bookmarks |
| POST | `/bookmarks/{userId}/{blogId}` | Add bookmark |
| DELETE | `/bookmarks/{userId}/{blogId}` | Remove bookmark |

---

## 👥 Followers

| Method | Endpoint | Description |
|--------|-----------|-------------|
| POST | `/followers/{followingId}/{followerId}` | Follow a user |
| DELETE | `/followers/{followingId}/{followerId}` | Unfollow a user |
| GET | `/followers/{userId}` | Get all followers of a user |

---

## 🔔 Notifications

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/notifications/user/{userId}` | Get user notifications |
| DELETE | `/notifications/{id}` | Delete notification |


---

## ⚙️ Caching Strategy

Redis caching is integrated to boost performance and reduce database load.

| Cache Key | Description |
|------------|-------------|
| `blogs::{id}` | Cached blog objects |
| `comments::{id}` | Cached individual comments |
| `blog_comments::{blogId}` | Caches **top 10 recent comments** for a blog |
| `notifications::{userId}` | Caches latest notifications per user |

When loading more comments, the next 10 are fetched directly from the database.

---
## 🧮 Database ERD

<img src="https://github.com/user-attachments/assets/1d1a18d3-2f88-4741-ab3c-df6a605adee8" width="500" />

<sub><i>Generated using reverse enginnering from MySQL Workbench.</i></sub>
---

## Data & Architecture

- **DTO Pattern** is used across the system:  
  - Each entity has a `ClassRequest` and `ClassResponse` DTO.
- **ORM**: JPA/Hibernate for database operations.  
- **Global Exception Handling** for consistent error messages.  
- **Validation** applied using Spring’s `@Valid` annotations.  
- **WebSocket Integration (planned)** for real-time notifications.

---

## Technologies Used

- **Spring Boot 3**
- **Spring Data JPA** (MySQL)
- **Spring Security (JWT)**
- **Redis Cache**
- **Docker Support**
- **Hibernate ORM**
- **Lombok**
- **WebSockets (planned for notifications)**

---

## Running the Application

### Using Maven
```bash
# Build and run
mvn clean install
mvn spring-boot:run
````

### Using Docker

```bash
# From the project root
docker build -t blogging-system -f 00-starter/Dockerfile .
docker run -p 8000:8080 blogging-system
```

Ensure Redis and MySQL are running locally or via Docker before launching.

---

## 🧮 Example Cache Flow

* On fetching a blog → Stored in cache as `blogs::{id}`.
* On fetching recent comments → Top 10 stored in `blog_comments::{blogId}`.
* On new comment → Cache auto-updates or refreshes top 10.
* Notifications → Stored under `notifications::{userId}`.

---

## 🧑‍💻 Author

**Ahmad Ouda**
Java Backend Developer | Competitive Programmer
📧 [ahmadouda383@gmail.com](mailto:ahmadouda383@gmail.com)
🔗 [LinkedIn](https://linkedin.com/in/real-ahmad-ouda)

---
