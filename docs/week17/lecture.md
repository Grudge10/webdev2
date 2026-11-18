# WEBDEV2 Week 17 Lecture Notes (F5)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**Branch:** `week17`  

## 1. Deployment Topology & Request Path

Vanilla HTML/CSS/JS (in `src/main/resources/static/`) is hosted within the Spring Boot `.jar` (`webdev2-0.0.1-SNAPSHOT.jar`) by Tomcat. The same-origin policy applies between the front-end (`http://localhost:8080/`) and back-end API (`/api/**`), and requests follow: `Browser (http://localhost:8080/) -> Tomcat (Spring SecurityFilterChain) -> RestControllers (Validation, DTOs) -> Service Layer (@Transactional) -> Hibernate JPA (ddl-auto=validate) -> MySQL 8.4.13 (vetclinic_app@localhost)`. In deployed environments (packaged), `app.js` uses dynamic `API_BASE = ""` (same origin); however, in local development when opened on `:5500`, it calls `http://localhost:8080`.

## 2. Configuration, Origin, Cookie, and Proxy Decisions

In `WebConfig.java`, hardcoded default values for multiple ports have been replaced with property `app.cors.allowed-origins` (environment variable `CORS_ALLOWED_ORIGINS`, default `http://localhost:8080`). When the application runs as a packaged application on the same origin, wide-open CORS is not enabled.

Session cookies configured with `server.servlet.session.cookie.http-only=true` and `same-site=lax` prevent JavaScript access and basic cross-site submissions. The `Secure` attribute for cookies can be toggled (`COOKIE_SECURE`) to `true` (on HTTPS) or `false` (only when testing locally using HTTP).

By setting `server.forward-headers-strategy` to the environment variable `FORWARD_HEADERS_STRATEGY` (default `none`), we avoid trusting spoofed `X-Forwarded-*` headers unless the application is explicitly deployed behind a known reverse proxy or load balancer.

The `GET /api/health` endpoint runs `SELECT 1` and returns JSON containing `status` and `database`, without exposing admin endpoints from Actuator or showing any DB URLs or credentials. Since this is only a liveness check (not a CRUD or transaction check), those functional checks are kept separate in UAT.

## 3. Schema Release Strategy & Least Privilege

SQL files to create schema (`01_schema.sql`), populate domain data (`02_seed.sql`), views (`03_views.sql`), and permissions (`04_access_control.sql`) are stored in `infoman1/week17/release/`. The maintainer installs these SQL files before startup.

At runtime, Spring Boot runs Hibernate in `validate` mode (`ddl-auto=validate`) as user `vetclinic_app@localhost` with `SELECT`, `INSERT`, `UPDATE`, and `DELETE` permissions. Users are created with passwords encoded via `BCryptPasswordEncoder`; passwords are not hardcoded in SQL files or Git and instead can be supplied as environment variables (`APP_SEED_*`).

## 4. UAT Finding & Recovery Limitations

During the backup/restore setup, attempting to `GRANT` access to the view `pet_appointment_report` within database `vetclinic_f5_restore` resulted in an error (`ERROR 1146`), as the view had not yet been created. A non-fatal warning was also logged during packaged startup (`Cannot find template location: classpath:/templates/`), because although Week 16 removed the Thymeleaf pages, the dependencies from the starter project were kept frozen. The `GRANT` issue was resolved by placing `GRANT` commands after restore in `setup.md`.

A full reproduction of this local packaged rehearsal (`webdev2-0.0.1-SNAPSHOT.jar` in `vetclinic_f5_restore`) can be run freely with no cloud hosting costs. However, automatic nightly remote backups are outside single-host local scope.
