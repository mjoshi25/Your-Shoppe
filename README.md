# ASMJ Shoppe

Multi-vendor marketplace built with Java 17, Spring Boot, Thymeleaf, Spring Security and MongoDB. Product and advertisement media use Cloudinary.

## Prerequisites
- JDK 17
- Maven 3.9+
- MongoDB Atlas or a local MongoDB instance

## Configuration
Configure secrets through environment variables; do not commit credentials. At minimum configure the MongoDB connection, Cloudinary credentials and the application bootstrap/admin settings required by your deployment. Review `src/main/resources/application.properties` for supported environment-variable names.

## Run locally (Windows PowerShell)
```powershell
mvn clean test
mvn spring-boot:run
```
Open `http://localhost:8080`.

## Build
```powershell
mvn clean package
```

## Deployment safety
This repository is source code. Test locally and review configuration before deployment. Never deploy to the production Render service without explicit owner approval. Never commit MongoDB, Cloudinary, UPI, JWT or admin credentials.

## Documentation
Only current setup and essential operational references are retained in this repository. Historical phase notes and temporary fix logs are intentionally excluded from the source ZIP.
