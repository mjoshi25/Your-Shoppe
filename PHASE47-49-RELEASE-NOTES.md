# ASMJ Shoppe – Combined Phases 47.1, 47, 48 and 49

- Phase 47.1: homepage service category card filtering.
- Phase 47: job openings and interview management.
- Phase 48: business directory.
- Phase 49: classifieds and second-hand products.
- Navigation: customer-facing discovery links grouped in the header; seller, employer, service provider and admin links grouped in dropdown/workspace areas.

## Security
- Public GET access to published jobs, approved businesses and approved classifieds.
- Submission and owner listing pages require authentication.
- Admin moderation endpoints require ADMIN role.
- Job application and employer workflows retain the existing Phase 47 security rules.

## Build and deployment
Run `mvn clean package -DskipTests` with Java 17. Verify MongoDB and Cloudinary configuration before deploying. No live deployment is performed by this package.
