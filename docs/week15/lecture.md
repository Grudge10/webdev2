# WEBDEV2 Week 15 Lecture Notes (F3)

**Name:** Jan Liam E. Arias  
**Student ID:** 2510499  
**Branch:** `week15`  

## 1. Permission Matrix and Role Boundary

| Action / Endpoint | Anonymous | `READER` (`clinic_viewer`) | `MANAGER` (`clinic_manager`) |
|---|---|---|---|
| Static UI, `GET /api/auth/csrf`, `POST /api/auth/login` | Allowed | Allowed | Allowed |
| `GET /api/auth/me`, `POST /api/auth/logout` | `401` | Allowed | Allowed |
| `GET /api/pets`, `/api/owners`, `/api/veterinarians`, `/api/appointments` | `401` | `200` | `200` |
| `POST` / `PUT` / `PATCH` / `DELETE` on `/api/pets`, `/api/owners`, `/api/appointments/book` | `401` | `403` | Allowed |

User identity is authenticated at `POST /api/auth/login` (`ClinicUserDetailsService`, `BCryptPasswordEncoder`), and user permissions according to their given role are enforced by Spring Security's `SecurityFilterChain`.

In `app.js`, `READER` users have hidden mutation forms and delete/edit buttons; however, if a `READER` sends an API request manually via `curl` or a modified DOM, Spring Security still enforces permissions based on HTTP method and path and returns a `403 Forbidden` status.

Pets, Owners, and Appointments within this clinic system are owned by shared clinic staff (`READER` and `MANAGER` roles). As this system has no public registration endpoint, roles cannot be assigned by clients. However, if public owner login functionality is added in the future, service methods would also check that `owner.id` matches the `ownerId` of the logged-in account.

## 2. Session Authentication and Password Handling

Static HTML, CSS, and JS resources in Spring Boot are served from the same origin (`http://localhost:8080`), meaning that Spring Security creates an `HttpOnly` / `SameSite=Lax` session cookie (`JSESSIONID`). No passwords, tokens, or session IDs are stored in `localStorage`. Passwords are hashed using an adaptive algorithm: `ClinicAccount` only stores `passwordHash` using `BCryptPasswordEncoder` (`$2a$10$...`), while `AuthUserDTO` only contains `username` and `role`. `AccountInitializer` uses optional local env variables (`APP_SEED_*`) during startup, preventing hardcoding or logging of raw passwords.

## 3. CSRF, CORS, Session, and DB Account Distinctions

In `WebConfig`, only dev origins (`http://localhost:3000`, `http://localhost:5500`, `http://localhost:8080`) are enabled for cross-origin requests, with credentials allowed (`allowCredentials(true)`). However, CORS does not provide authentication for users and does not block writes from non-browsers (`curl`).

In `SecurityConfig`, CSRF tokens are stored in the session (`HttpSessionCsrfTokenRepository`, `CsrfTokenRequestAttributeHandler`). In `app.js`, the CSRF token is fetched from `/api/auth/csrf` and stored in `csrfState`. The `X-CSRF-TOKEN` header is added for all `POST`/`PUT`/`PATCH`/`DELETE` requests (including login/logout), and a new rotated token (rotated in `AuthController.login`) is fetched immediately after login and logout.

The datasource in Spring Boot is connected to MySQL as `vetclinic_app@localhost` (with only `SELECT`, `INSERT`, `UPDATE`, `DELETE` permissions and `spring.jpa.hibernate.ddl-auto=validate`), and `root` is not used. In Spring Security, HTTP endpoints require either `READER` or `MANAGER` roles based on the user.

## 4. Security Filter Configuration Walkthrough

In `SecurityConfig.java`, CORS is enabled with defaults using `cors(Customizer.withDefaults())`, and the `SecurityContext` object created by authentication in `AuthController.login()` is stored in `HttpSession` via `HttpSessionSecurityContextRepository`. `authenticationEntryPoint` returns `401` JSON rather than redirecting `fetch()` requests to an HTML login page, and if a user has an incorrect role or an invalid/missing CSRF token, `accessDeniedHandler` returns `403` JSON. Tested cases in `security_curl_output.txt` confirmed that both error scenarios returned a `403` response: a `MANAGER` user with no `X-CSRF-TOKEN` received `403`, and a `READER` user with a valid rotated `X-CSRF-TOKEN` also received `403`.
