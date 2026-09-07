# Blogaty — Full-Stack Blogging Platform

**Blogaty** is a modern, full-stack blogging web application consisting of a **Spring Boot 3 RESTful API** and a **React (TypeScript + Vite) frontend**. It provides user authentication, blog publishing, comments, upvoting/downvoting, bookmarks, following/followers, real-time Server-Sent Events (SSE) notifications, and search capabilities.

---

## 🚀 Quick Start & Running the Project

### Prerequisites
- **Docker** & **Docker Compose**
- *(Optional for local dev without Docker)*: JDK 21, Maven 3.9+, Node.js 20+

### 1. Running with Docker & Nginx Gateway (Recommended)

Start the entire stack (MySQL, Redis, Spring Boot Backend, React Frontend, and Nginx Gateway) with a single command:

```bash
# 1. Generate local SSL certificate (if not already generated)
chmod +x nginx/generate-ssl.sh && ./nginx/generate-ssl.sh

# 2. Launch the entire stack
docker compose up -d --build
```

- **Web Application (HTTPS):** [https://localhost](https://localhost)
- **HTTP (Auto-redirects to HTTPS):** [http://localhost](http://localhost)
- **Swagger UI (via Nginx):** [https://localhost/swagger-ui/index.html](https://localhost/swagger-ui/index.html)

To stop all services:
```bash
docker compose down
```

To check service health and logs:
```bash
docker compose ps
docker compose logs -f backend
```

### 2. Local Development Setup

#### Backend (`00-starter`):
```bash
cd 00-starter
./mvnw clean spring-boot:run
```
Backend API will be accessible at: **[http://localhost:8080](http://localhost:8080)**  
OpenAPI / Swagger UI docs: **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

#### Frontend (`frontend-react-app`):
```bash
cd frontend-react-app
npm install
npm run dev
```
Frontend web application will be accessible at: **[http://localhost:5173](http://localhost:5173)**

---

## 🔄 CI/CD Pipeline (GitHub Actions)

The repository includes an automated GitHub Actions CI/CD pipeline (`.github/workflows/ci-cd.yml`) triggered on every push, enforcing 4 sequential quality gates:

```
[ Push to Git ]
       │
       ▼
Gate 1: Lint & Code Quality   (Java compilation + Frontend TypeScript typecheck)
       │ (Pass)
       ▼
Gate 2: Automated Testing     (Backend JUnit 5 & Mockito + Frontend tests)
       │ (Pass)
       ▼
Gate 3: Security Scanning     (Aqua Security Trivy Vulnerability & Secret Scan)
       │ (Pass)
       ▼
Gate 4: Build & Push          (Multi-stage Docker build & push to Docker Hub)
```

### Required GitHub Secrets
Configure the following secrets in **Repository Settings &rarr; Secrets and variables &rarr; Actions**:
- `DOCKERHUB_USERNAME`: Your Docker Hub username or organization.
- `DOCKERHUB_TOKEN`: Your Docker Hub Personal Access Token (PAT).

---

## 🧱 Project Architecture

The backend follows a **Layered Onion Architecture** with clear separation of concerns:

- **Entity Layer (`com.blog.demo.entity`):**  
  Domain models mapped via JPA/Hibernate (`User`, `Blog`, `Comment`, `BlogVote`, `CommentVote`, `Bookmark`, `Follower`, `Notification`). Composite primary keys (`BlogVoteID`, `CommentVoteID`, `BookmarkID`, `FollowerID`) use `@Embeddable` with scalar IDs, linked via `@MapsId` relationships.
- **Repository Layer (`com.blog.demo.repository`):**  
  Spring Data JPA repositories extending `JpaRepository` with custom JPQL queries.
- **Service Layer (`com.blog.demo.service`):**  
  Clean interfaces with concrete `@Service` implementations handling business logic, transactions (`@Transactional`), caching, and notification triggers.
- **DTO Layer (`com.blog.demo.dto`):**  
  Encapsulated Request and Response DTOs protecting entity internals from API exposure.
- **Security & Controller Layer (`com.blog.demo.security`, `com.blog.demo.controller`):**  
  Stateless JWT authentication filter, CORS configuration, and REST endpoints.

---

## 📡 API Endpoints Reference

### 🔐 Authentication & Users
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/users/register` | Register a new user | Public |
| `POST` | `/users/login` | Login and obtain JWT token | Public |
| `GET` | `/users` | List all users | ADMIN |
| `GET` | `/users/{userId}` | Get user by ID | USER, ADMIN |
| `PATCH` | `/users` | Update user profile | USER, ADMIN |
| `DELETE` | `/users/{userId}` | Delete user | ADMIN |

Include the JWT in the header for protected endpoints:
```http
Authorization: Bearer <your_jwt_token>
```

### 📝 Blogs
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/blogs` | Get paginated blogs (`?page=0&size=10`) | Public |
| `GET` | `/blogs/{blogId}` | Get single blog by ID | Public |
| `GET` | `/blogs/user/{userId}` | Get blogs written by a specific user | USER, ADMIN |
| `POST` | `/blogs/user/{userId}` | Create a new blog post | USER, ADMIN |
| `PATCH` | `/blogs/{blogId}` | Update a blog post | USER, ADMIN |
| `DELETE` | `/blogs/{blogId}` | Delete a blog post | ADMIN |

### 💬 Comments
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/comments/blog/{blogId}?page=0&size=10` | Get paginated comments for a blog | Public |
| `GET` | `/comments/{commentId}` | Get single comment by ID | USER, ADMIN |
| `POST` | `/comments/{userId}/{blogId}` | Add a comment to a blog | USER, ADMIN |
| `PATCH` | `/comments` | Edit a comment | USER, ADMIN |
| `DELETE` | `/comments/{commentId}` | Delete a comment | ADMIN |

### 👍 Votes
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/votes/blog/{blogId}` | Get all votes for a blog | Public |
| `GET` | `/votes/blog/{userId}/{blogId}` | Get user's vote status on a blog | USER, ADMIN |
| `POST` | `/votes/blog/{userId}/{blogId}` | Vote on a blog (`up` or `down`) | USER, ADMIN |
| `PATCH` | `/votes/blog/{userId}/{blogId}` | Update vote on a blog | USER, ADMIN |
| `GET` | `/votes/comment/{commentId}` | Get all votes for a comment | Public |
| `GET` | `/votes/comment/{userId}/{commentId}` | Get user's vote status on a comment | USER, ADMIN |
| `POST` | `/votes/comment/{userId}/{commentId}` | Vote on a comment (`up` or `down`) | USER, ADMIN |
| `PATCH` | `/votes/comment/{userId}/{commentId}` | Update vote on a comment | USER, ADMIN |

### 🔖 Bookmarks
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/bookmarks/user/{userId}` | Get all bookmarked blogs for a user | USER, ADMIN |
| `POST` | `/bookmarks/{userId}/{blogId}` | Bookmark a blog | USER, ADMIN |
| `DELETE` | `/bookmarks/{userId}/{blogId}` | Remove a bookmark | USER, ADMIN |

### 👥 Followers & Followings
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/followers/{userId}` | Get list of followers for a user | USER, ADMIN |
| `GET` | `/followers/by/{userId}` | Get list of users followed by a user | USER, ADMIN |
| `GET` | `/followers/numbers/{userId}` | Get followers and following counts | USER, ADMIN |
| `GET` | `/followers/suggest/{userId}` | Get follow suggestions | USER, ADMIN |
| `POST` | `/followers/{receiverId}/{actorId}` | Follow a user | USER, ADMIN |
| `DELETE` | `/followers/{receiverId}/{actorId}` | Unfollow a user | USER, ADMIN |

### 🔔 Notifications (SSE)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/notifications/stream/{userId}` | Real-time Server-Sent Events (SSE) notification stream | Public |
| `GET` | `/notifications/user/{userId}` | Get list of notifications and unread count | USER, ADMIN |
| `DELETE` | `/notifications/{id}` | Delete a notification | USER, ADMIN |

### 🔍 Search
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/search/blogs?search={term}&page=0&size=10` | Search blog content | Public |
| `GET` | `/search/users?search={term}&page=0&size=10` | Search users by username | Public |

---

## ⚙️ Caching Strategy (Redis)

Spring Cache with Redis integration reduces database load:

| Cache Region | Key | Description |
|---|---|---|
| `blogs` | `#blogId` | Single blog cache |
| `comments` | `#commentId` | Single comment cache |
| `blog_comments` | `#blogId` | Top recent comments per blog |
| `notifications` | `#userId` | User notifications and unread count |

---

## 🛠️ Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Data JPA & Hibernate 6**
- **Spring Security 6 (Stateless JWT)**
- **MySQL 8.0**
- **Redis 7** (Caching & Message Pub/Sub)
- **SpringDoc OpenAPI (Swagger UI)**
- **Lombok**

### Frontend
- **React 18**
- **TypeScript**
- **Vite 4**
- **React Router v7**
- **Bootstrap 5**
- **Axios**

---

## 🧑‍💻 Author

**Ahmad Ouda**  
Java Backend Developer | Competitive Programmer  
📧 [ahmadouda383@gmail.com](mailto:ahmadouda383@gmail.com)  
🔗 [LinkedIn](https://linkedin.com/in/real-ahmad-ouda)
