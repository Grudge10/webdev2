# WebDev 2 — Week 2: Spring Framework & Spring Boot Fundamentals

This project is a showcase of the Spring Framework and Spring Boot fundamentals, utilizing project kickstarting with Spring Boot, Spring-managed objects (beans) using @Service and @Repository annotations, injecting dependencies via constructors, configuring external properties with @ConfigurationProperties, and exploring CommandLineRunner for startup tasks.

---

## How to Build the Application

To clean and compile the application, run:

```bash
./mvnw clean package
```

## How to Run the Application

To boot the Spring Boot application and start the embedded Tomcat server:

```bash
./mvnw spring-boot:run
```

(On Windows Command Prompt/PowerShell, use .\mvnw.cmd spring-boot:run)

## Application Port

The application runs on port 8082, as configured in src/main/resources/application.properties:

```properties
server.port=8082
```

## Tasks Overview

- **Task 1 — Bootstrap the Project:** Created a new Spring Boot application via Spring Initializr, bundled with Spring Web, and verified it starts on port 8082.
- **Task 2 — Service & Repository Beans:** Built an encapsulated `Product` class, an in-memory `ProductRepository` annotated with `@Repository`, and a `ProductService` annotated with `@Service` containing threshold filtering logic.
- **Task 3 — Constructor Injection:** Supplied `ProductRepository` into `ProductService` using constructor injection rather than manual instantiation.
- **Task 4 — Externalized Configuration:** Created a `ShopProperties` class using `@ConfigurationProperties(prefix = "shop")` to bind shop name and currency from `application.properties`.
- **Task 5 — Startup Wiring Report:** Implemented a `CommandLineRunner` bean (`ProductReportRunner`) that automatically executes upon application startup to display the configured shop details and filtered products.

## Sample Report Output

```text
================================
PRODUCT REPORT
================================
Shop: My Web Store
Currency: PHP

Products above PHP 5000:
Laptop - PHP 45000
Monitor - PHP 12000
Printer - PHP 8500
================================
```
