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
# 1. Set up environment variables & secrets
cp .env.example .env

# 2. Generate local SSL certificate (if not already generated)
chmod +x nginx/generate-ssl.sh && ./nginx/generate-ssl.sh

# 3. Launch the entire stack
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
Gate 1: Lint & Static Analysis (Backend Checkstyle + Frontend TypeScript typecheck)
       │ (Pass)
       ▼
Gate 2: Automated Testing     (Backend JUnit 5 & Mockito + Frontend tests)
       │ (Pass)
       ▼
Gate 3: Security Scanning     (Trivy Vulnerability & Secret Scan — exit-code: 1 blocking gate)
       │ (Pass)
       ▼
Gate 4: Build & Push          (Multi-stage Docker build & push to Docker Hub on main branch)
```

| Quality Gate | Job | Tools & Checks | Policy |
| :--- | :--- | :--- | :--- |
| **Gate 1** | `lint` | Maven Checkstyle (`checkstyle.xml`) & Frontend `tsc --noEmit` | Fails on code style/type errors |
| **Gate 2** | `test` | Spring Boot JUnit 5 / Mockito & Frontend tests | Must pass with 0 failures |
| **Gate 3** | `scan` | Aqua Security Trivy (SAST, Dependencies & Secrets) | Blocks on CRITICAL/HIGH CVEs |
| **Gate 4** | `build-and-push` | Docker Buildx with GitHub Actions layer cache | Only runs on verified `main` commits |

### Required GitHub Secrets
Configure the following secrets in **Repository Settings &rarr; Secrets and variables &rarr; Actions**:
- `DOCKERHUB_USERNAME`: Your Docker Hub username or organization.
- `DOCKERHUB_TOKEN`: Your Docker Hub Personal Access Token (PAT).

> **Note:** Application credentials (database passwords, root password, and JWT secret) are managed via your local or staging `.env` file (copied from `.env.example`) and are not required in GitHub Secrets for standard CI build runs.

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

---

## 🧪 Testing & Code Quality Locally

You can run each quality gate locally exactly as it executes in CI:

```bash
# 1. Backend Static Analysis (Checkstyle)
mvn -f 00-starter/pom.xml checkstyle:check

# 2. Backend Automated Tests (JUnit 5 & Mockito)
mvn -f 00-starter/pom.xml test

# 3. Frontend Type-Check & Lint
npm --prefix frontend-react-app run lint

# 4. Frontend Production Build
npm --prefix frontend-react-app run build
```

---

## 📁 Repository Structure

```
Blogaty/
├── .github/
│   └── workflows/
│       └── ci-cd.yml             # 4-Stage GitHub Actions CI/CD Pipeline
├── 00-starter/                   # Spring Boot 3.4 RESTful API
│   ├── src/
│   │   ├── main/java/com/blog/   # Controllers, Services, Entities, DTOs, Security
│   │   ├── main/resources/       # application.yml
│   │   ├── test/java/com/blog/   # JUnit 5 & Mockito Unit/Integration Tests
│   │   └── test/resources/       # application-test.yml (Isolated H2 Test Profile)
│   ├── checkstyle.xml            # Checkstyle Static Analysis Ruleset
│   ├── Dockerfile                # Multi-stage container build (temurin-21 JRE)
│   └── pom.xml                   # Maven configuration
├── frontend-react-app/           # React 18 + TypeScript + Vite UI
│   ├── src/                      # Components, Pages, API clients, Hooks
│   ├── Dockerfile                # Frontend development & build container
│   ├── package.json              # NPM dependencies & scripts (lint, test, build)
│   └── vite.config.ts
├── nginx/                        # Nginx Reverse Proxy & Load Balancer
│   ├── nginx.conf                # Routing, SSL termination, least_conn load balancing
│   └── generate-ssl.sh           # Local self-signed SSL certificate generator
├── .env.example                  # Environment configuration & secrets template
├── docker-compose.yml            # Multi-service stack (MySQL, Redis, Backend, Frontend, Nginx)
├── blog-sql-builder.sql          # Initial database schema & seed data
├── .trivyignore                  # DevSecOps baseline security configuration
├── .gitignore                    # Ignored build artifacts, local dev certs, and .env
└── README.md
```

---

## 🛠️ Tech Stack

### Gateway & DevOps
- **Nginx 1.27 (Alpine)** — Reverse proxy, SSL/TLS termination, `least_conn` load balancing, HTTP-to-HTTPS redirect
- **Docker & Docker Compose** — Containerized multi-service orchestration
- **GitHub Actions** — 4-stage sequential CI/CD quality gates (Lint &rarr; Test &rarr; Scan &rarr; Build)
- **Trivy (Aqua Security)** — Static Application Security Testing (SAST) & vulnerability scanner
- **Checkstyle** — Java static code analysis & style enforcement

### Backend
- **Java 21**
- **Spring Boot 3.4.13**
- **Spring Data JPA & Hibernate 6**
- **Spring Security 6 (Stateless JWT)**
- **MySQL 8.0**
- **Redis 7** (Caching & Message Pub/Sub)
- **SpringDoc OpenAPI (Swagger UI)**
- **JUnit 5 & Mockito** (Automated testing with H2 in-memory DB)
- **Lombok**

### Frontend
- **React 18**
- **TypeScript**
- **Vite 4**
- **React Router v7 (7.18.x)**
- **Bootstrap 5 & MDBReact**
- **Axios**

---

## 🧑‍💻 Authors

**Ahmad Ouda**  
Full-Stack Developer — Backend, Frontend & Database  
📧 [ahmadouda383@gmail.com](mailto:ahmadouda383@gmail.com)  
🔗 [LinkedIn](https://linkedin.com/in/real-ahmad-ouda)

**Ahmed Zayan**  
DevOps Engineer — CI/CD, Docker, Security & Infrastructure  
📧 [azayan570@gmail.com](mailto:azayan570@gmail.com)  
🔗 [LinkedIn](https://www.linkedin.com/in/ahmed-zayan-33755919a/)
