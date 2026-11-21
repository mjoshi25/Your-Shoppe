
# Multi-stage Java 17 build for Render
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn -B -q dependency:go-offline

# Copy application source
COPY src ./src

# Build Spring Boot application
RUN mvn -B -DskipTests package

# Verify packaged authentication templates
RUN set -eu; \
    JAR=/workspace/target/asmj-marketplace-0.0.1-SNAPSHOT.jar; \
    echo "========================================"; \
    echo "VERIFYING PACKAGED HTML TEMPLATES"; \
    echo "========================================"; \
    jar tf "$JAR" | grep -E 'BOOT-INF/classes/templates/.*\.html$$' > /tmp/packaged-templates.txt; \
    echo "Total packaged HTML templates:"; \
    wc -l < /tmp/packaged-templates.txt; \
    echo "Checking login and registration templates..."; \
    jar tf "$JAR" | grep -E 'BOOT-INF/classes/templates/auth/(login|register)\.html$$'; \
    mkdir -p /tmp/jar-check; \
    cd /tmp/jar-check; \
    jar xf "$JAR" \
      BOOT-INF/classes/templates/auth/login.html \
      BOOT-INF/classes/templates/auth/register.html; \
    echo "Login stylesheet reference:"; \
    grep -n 'app.css' BOOT-INF/classes/templates/auth/login.html; \
    echo "Registration stylesheet reference:"; \
    grep -n 'app.css' BOOT-INF/classes/templates/auth/register.html; \
    echo "Registration heading:"; \
    grep -n 'JOIN THE COMMUNITY' BOOT-INF/classes/templates/auth/register.html; \
    echo "Template verification completed."

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root application user
RUN useradd --system --uid 10001 appuser

# Copy packaged Spring Boot JAR
COPY --from=build /workspace/target/*.jar /app/app.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
