
---

### 1. Build Configuration & Maven (`pom.xml`)

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Invalid Spring Boot Parent Version** (`3.5.6`) | Spring Boot `3.5.6` does not exist. Maven failed immediately upon reading the POM, preventing any dependency resolution or builds. | Changed to the latest stable version **`3.4.1`**. |
| **Invalid Version Token** (`RELEASE`) for `org.jetbrains:annotations` | Dynamic version strings like `RELEASE` are deprecated and rejected by modern Maven compiler plugins and repositories. | Replaced with explicit stable version **`24.0.1`**. |

---

### 2. Infrastructure & Application Configuration (`application.yml`)

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Hardcoded Database & Redis Credentials** | Containerized deployments could not pass environment variables dynamically; running in Docker failed because hostnames like `localhost` don't point to container services. | Externalized all sensitive properties using Spring placeholders with sensible defaults: `${DB_URL:...}`, `${DB_USERNAME:...}`, `${DB_PASSWORD:...}`, `${REDIS_HOST:...}`, `${REDIS_PORT:...}`, `${JWT_SECRET:...}`. |
| **Missing Hibernate DDL Validation & Dialect** | Without explicit `ddl-auto: validate` and dialect declaration, schema mismatch issues could silently fail or corrupt data on startup. | Added `spring.jpa.hibernate.ddl-auto: validate` and `spring.jpa.properties.hibernate.dialect: org.hibernate.dialect.MySQLDialect`. |
| **Open-In-View Default Warning** | `spring.jpa.open-in-view` was left enabled, risking database connection leakage during view rendering. | Explicitly disabled via `spring.jpa.open-in-view: false`. |

---

### 3. Backend Dockerfile (`00-starter/Dockerfile`)

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Full JDK Base Image Used for Runtime** | Image was bloated (~800MB) using `eclipse-temurin:21-jdk` just to run an already compiled JAR. | Switched base image to lightweight JRE: `eclipse-temurin:21-jre` (~200MB). |
| **Broken `ARG` Usage in `COPY`** | `ARG JAR_FILE=target/*.jar` was defined, but `COPY` used a literal path instead of the variable `${JAR_FILE}`. | Updated to `COPY ${JAR_FILE} app.jar`. |
| **Missing Default Environment Variables** | Containers launched without explicit `-e` flags crashed on startup trying to connect to non-existent default hosts. | Added container-level `ENV` defaults for `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, and `JWT_SECRET`. |

---

### 4. JPA Entities & Composite Keys

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Illegal `@ManyToOne` Inside `@Embeddable` (`BlogVoteID` & `CommentVoteID`)** | **Critical crash**: Hibernate strictly forbids entity relationship annotations (`@ManyToOne`) inside an `@Embeddable` composite primary key class. Application crashed on startup. | Removed `@ManyToOne User/Blog/Comment` from the ID classes. Replaced with raw scalar IDs (`Long userId`, `Long blogId`, `Long commentId`). Added proper `@ManyToOne` with `@MapsId` on the parent entities (`BlogVote` and `CommentVote`). |
| **Primitive `int` Entity IDs (`User`, `Blog`, `Comment`)** | Entities declared `int id` / `int blogId`, but Spring Data repositories and JPA standards expect `Long`. This caused type-mismatch compile and runtime errors throughout queries and DTO conversions. | Migrated all entity primary keys to **`Long`**. |
| **Lombok `@Data` on `User` (`UserDetails`)** | `@Data` generates `equals()`, `hashCode()`, and `toString()` including lazy-loaded collections (`blogs`, `comments`, `followers`). Calling them triggered `StackOverflowError` (infinite recursion) and `LazyInitializationException`. | Replaced `@Data` with `@Getter`, `@Setter`, `@ToString(exclude = ...)`, and `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` on `id`. |
| **Missing Column Mappings on `Blog.java`** | Fields `date`, `votes`, and `comments` lacked explicit `@Column` annotations. | Added explicit `@Column` annotations. |
| **Boolean Property Collision on `Notification.java`** | Field named `boolean isRead` caused Lombok and Jackson getter/setter name clashes (`isRead()`, `getIsRead()`). | Renamed field to `boolean read` and mapped to database column `@Column(name = "is_read")`. |
| **Unused / Invalid Imports** | Entities contained unused imports (e.g. `jakarta.persistence.Id` inside composite ID classes; unused `java.util.Date`). | Cleaned up all entity imports. |

---

### 5. Repositories (`com.blog.demo.repository.*`)

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Non-Existent Method in `UserRepository`** | `findUsernameById(int id)` attempted to query a property that does not exist in the domain model, triggering Spring Data's `PropertyReferenceException` and aborting startup. | Removed the invalid method. |
| **Overloaded Method Ambiguity in `UserRepository`** | Having both `findByUsername(String)` and `findByUsername(String, PageRequest)` caused query derivation conflicts. | Renamed the paginated method to `findByUsernameContaining(String, PageRequest)`. |
| **Illegal `@Param` Import in `CommentRepository`** | Imported `io.lettuce.core.dynamic.annotation.Param` (Redis Lettuce client) instead of Spring Data's `@Param`. Spring Data JPA could not bind query parameters to JPQL `:blogId`. | Replaced with `org.springframework.data.repository.query.Param`. |
| **Method Collision in `CommentRepository`** | Custom `Comment findById(int)` clashed with `CrudRepository.findById(ID)`. | Renamed to `Optional<Comment> findCommentById(Long id)`. |
| **Invalid Generic ID Types in Repositories** | `BlogVoteRepository`, `CommentVoteRepository`, `BookmarkRepository`, and `FollowerRepository` extended `JpaRepository<Entity, Long>` instead of their composite key types. | Updated to `JpaRepository<BlogVote, BlogVoteID>`, `<CommentVote, CommentVoteID>`, `<Bookmark, BookmarkID>`, and `<Follower, FollowerID>`. |
| **Inverted Query Logic in `FollowerRepository`** | `findFollowersIdByUserId` returned following IDs and `findFollowingsIdByUserId` returned follower IDs (swapped actor vs. receiver). | Corrected JPQL queries to accurately return actors for followers and receivers for followings. |
| **Missing Annotation on `NotificationRepository`** | Lacked the `@Repository` stereotype annotation. | Added `@Repository`. |

---

### 6. Service Interfaces & Implementations

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **`@Service` on Interfaces** | `@Service` was placed on interface declarations (`UserService`, `BlogService`, etc.) instead of solely on their concrete implementation classes. | Removed `@Service` from all 8 service interfaces. |
| **Compilation Error: `java.awt.print.Pageable`** in `CommentServiceImpl` | Accidental import of desktop AWT printing API instead of Spring Data's `Pageable`. | Removed the invalid import and used `PageRequest`. |
| **Unsafe Entity Merging via `ObjectMapper`** (`BlogServiceImpl` & `CommentServiceImpl`) | Methods converted entity instances to `ObjectNode`, merged maps, and converted back. This erased unmapped fields, broke JPA proxy references, and triggered detached entity exceptions. | Replaced with direct, explicit field setters (`blog.setContent(...)`, `blog.setDate(...)`). |
| **Duplicate Method Invocations in `VoteServiceImpl`** | `blogService.updateBlogVoteCount(blogVote)` and `commentService.updateCommentVoteCount(commentVote)` were duplicated back-to-back, double-counting votes on every vote update. | Removed the duplicate calls. |
| **Wrong `targetId` in `sendNotification`** (`CommentServiceImpl`) | Comments sent notifications pointing to the comment author's ID rather than the `blogId`, breaking notification click navigation. | Updated `targetId` to point to `comment.getBlog().getBlogId()`. |
| **Unsafe `Optional.get()` Calls** | Services called `.get()` directly on database lookups without checking existence, crashing with `NoSuchElementException`. | Replaced all `.get()` calls with `.orElseThrow(() -> new GlobalException(...))`. |
| **Unread Notification Counter Bug** (`NotificationServiceImpl`) | `unreadNotifications` was populated with `(long) notifications.size()` (the total count) rather than filtering by unread status. | Fixed to count unread notifications: `notifications.stream().filter(n -> !n.isRead()).count()`. |
| **Dead `RedisConfig` Injections** | `BookmarkServiceImpl` and `FollowServiceImpl` injected unused `RedisConfig` instances into fields that were never referenced. | Removed unused imports, fields, and constructor parameters. |

---

### 7. Security & Utilities

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Missing `@EnableWebSecurity`** in `SecurityConfig` | Security filter chains were not being applied properly by Spring Security 6. | Added `@EnableWebSecurity`. |
| **Stateful Session Default** | Spring Security was creating HTTP sessions for requests instead of enforcing stateless JWT behavior. | Added `http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`. |
| **Hardcoded Secret Key in `JwtUtil`** | Secret key was a hardcoded static string in source code and could not be rotated via configuration or environment variables. | Injected dynamically via `@Value("${jwt.secret}")` and made validation methods `public`. |
| **`userRepository` Public Field in `ApplicationConfig`** | Field had `public` visibility, breaking encapsulation. | Changed to `private final`. |

---

### 8. Controllers & DTOs

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **`@RequestBody` on HTTP GET Requests** (`SearchController`) | HTTP GET requests typically do not have a request body. Many HTTP clients, proxies, and web browsers strip bodies on GET, making search fail. | Replaced `@RequestBody Map<String, String>` with `@RequestParam String search`. |
| **Missing Leading Slash** (`BlogController`) | `@PostMapping("user/{userId}")` lacked a leading slash. | Updated to `@PostMapping("/user/{userId}")`. |
| **DTO Field Types Out of Sync** | `BlogResponse`, `CommentResponse`, `UserResponse`, `BlogVoteResponse`, and `CommentVoteResponse` still had primitive `int` IDs or attempted to navigate removed composite key fields (`blogVote.getId().getUser()`). | Updated DTO IDs to `Long` and navigated relationships directly on entities (`blogVote.getUser()`). |

---

### 9. Frontend React Application (`frontend-react-app`)

| What Was Wrong | Why It Broke the System | How It Was Fixed |
|---|---|---|
| **Missing Packages in `package.json`** | `axios` and `bootstrap` were imported in code (`apiClient.ts` and `main.tsx`) but missing from `package.json`. The container crashed on startup with `Failed to resolve import`. | Added `axios` and `bootstrap` to `package.json` dependencies and ran `npm install`. |
| **TypeScript Compilation Bug** in `BlogList.tsx` | Line 27 called `getBlogs(page, 10, userId)` with 3 parameters, but `getBlogs` only accepted 2. | Updated to conditionally call `getBlogByUser(userId)` when a user ID filter is present. |
| **Missing Routes for Bookmarks & Settings** | The sidebar and top navbar had buttons navigating to `/bookmarks` and `/settings`. Because these routes did not exist in `App.tsx`, React Router hit the wildcard `<Route path="*" element={<Navigate to="/login" />} />` and immediately kicked the user back to the login screen. | Built [src/api/bookmarkApi.ts](file:///home/ahmedz/projects/Blogaty/frontend-react-app/src/api/bookmarkApi.ts), [src/pages/BookmarksPage.tsx](file:///home/ahmedz/projects/Blogaty/frontend-react-app/src/pages/BookmarksPage.tsx), [src/pages/SettingsPage.tsx](file:///home/ahmedz/projects/Blogaty/frontend-react-app/src/pages/SettingsPage.tsx), and registered their routes in `App.tsx`. |
| **Storage Inconsistency (`localStorage` vs `sessionStorage`)** | Auth tokens were stored in `localStorage`, but `userId` and `username` were only in `sessionStorage`. Opening new tabs or refreshing with an active token left `userId = null / 0`, breaking profile links, voting, and follow actions. | Updated `authApi.ts`, `useAuth.ts`, and `Layout.tsx` to store and read auth state across both `localStorage` and `sessionStorage`. |
| **Missing Frontend Dockerfile** | `frontend-react-app` had no Dockerfile in the project to support clean local rebuilds. | Created [frontend-react-app/Dockerfile](file:///home/ahmedz/projects/Blogaty/frontend-react-app/Dockerfile) and rebuilt `blogaty-frontend:latest`. |

---

### Final Verified State

- **Backend**: Compiles with zero errors (`mvn clean package`), starts in ~5 seconds, connects to MySQL & Redis, and serves all REST endpoints cleanly.
- **Frontend**: Compiles with zero TypeScript errors (`tsc && vite build`), connects seamlessly to the backend on port `8080`, and routes all buttons (Home, Bookmarks, Followers, Notifications, Profile, Settings) properly without unintended logouts.