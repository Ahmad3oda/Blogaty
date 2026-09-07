FROM eclipse-temurin:21-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV DB_URL=jdbc:mysql://db:3306/blog_directory
ENV DB_USERNAME=ouda
ENV DB_PASSWORD=password
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV JWT_SECRET=bVYp7uF83m6Zs8jF4kLd9xP0rTn2qWe5yHcXzBvN8mRq3sGfD1aJkLpOeRtYwUi
ENTRYPOINT ["java", "-jar", "/app.jar"]