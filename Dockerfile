# Multi-stage Java 17 build for Render
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package
RUN echo "=== LOGIN TEMPLATE IN JAR ===" && \
    unzip -p target/asmj-marketplace-0.0.1-SNAPSHOT.jar BOOT-INF/classes/templates/auth/login.html | grep -E 'app.css|auth-showcase' && \
    echo "=== REGISTER TEMPLATE IN JAR ===" && \
    unzip -p target/asmj-marketplace-0.0.1-SNAPSHOT.jar BOOT-INF/classes/templates/auth/register.html | grep -E 'app.css|JOIN THE COMMUNITY'

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 10001 appuser
COPY --from=build /workspace/target/*.jar /app/app.jar
USER appuser
EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
