# WEBDEV2 Setup and Release Guide (F5)

## 1. Prerequisites
- OpenJDK `21.0.13` (`JAVA_HOME` pointing to JDK 21; Maven compiles with target `17`)
- MySQL `8.4.13` (InnoDB enabled)
- Maven Wrapper (`./mvnw` on macOS/Linux or `mvnw.cmd` on Windows)

## 2. Database Schema & Least-Privilege Provisioning (`infoman1/week17/release/`)
Never run the runtime application as MySQL `root`. As schema maintainer:

```bash
$HOME/dev-tools/mysql/bin/mysql -uroot -h127.0.0.1 -P3307 -e "CREATE DATABASE vetclinic_f5_release;"
$HOME/dev-tools/mysql/bin/mysql -uroot -h127.0.0.1 -P3307 vetclinic_f5_release < ../infoman1/week17/release/01_schema.sql
$HOME/dev-tools/mysql/bin/mysql -uroot -h127.0.0.1 -P3307 vetclinic_f5_release < ../infoman1/week17/release/02_seed.sql
$HOME/dev-tools/mysql/bin/mysql -uroot -h127.0.0.1 -P3307 vetclinic_f5_release < ../infoman1/week17/release/03_views.sql
```

Substitute `<TARGET_DB>` (`vetclinic_f5_release`) and strong local passwords in a local copy of `04_access_control.sql` (outside Git) and execute it as admin to grant `vetclinic_app@localhost` (`SELECT, INSERT, UPDATE, DELETE` only) and `vetclinic_report@localhost` (`SELECT` on `pet_appointment_report`).

## 3. Environment Configuration (`application-example.properties`)
Required runtime variables (never commit real passwords):
- `DB_URL` (e.g. `jdbc:mysql://localhost:3307/vetclinic_f5_release`)
- `DB_USERNAME` (`vetclinic_app`)
- `DB_PASSWORD` (local password for `vetclinic_app`)
- `JPA_DDL_AUTO` (`validate`)
- `SERVER_PORT` (`8080`)
- `CORS_ALLOWED_ORIGINS` (`http://localhost:8080`, explicit origins only)
- `COOKIE_SECURE` (`false` for HTTP `localhost`, `true` behind HTTPS)
- `FORWARD_HEADERS_STRATEGY` (`none` by default; `native` or `framework` only behind a trusted proxy)
- Initial account seeding (first boot only, then omit): `APP_SEED_READER_USERNAME`, `APP_SEED_READER_PASSWORD`, `APP_SEED_MANAGER_USERNAME`, `APP_SEED_MANAGER_PASSWORD`

## 4. Build, Package, and Run

1. Prepare `vetclinic_f4_test` (using `01_schema.sql`–`03_views.sql` and granting `vetclinic_app`), then run clean build and automated tests:
   ```bash
   DB_USERNAME=vetclinic_app DB_PASSWORD='<LOCAL_APP_PASSWORD>' \
   JAVA_HOME=$HOME/dev-tools/jdk-21.0.13+9/Contents/Home ./mvnw clean verify
   ```
   Output artifact: `target/webdev2-0.0.1-SNAPSHOT.jar`

2. Start the packaged release (set `APP_SEED_*` on first boot to insert `BCrypt`-hashed test users, then omit on subsequent restarts):
   ```bash
   DB_URL='jdbc:mysql://localhost:3307/vetclinic_f5_release' \
   DB_USERNAME=vetclinic_app DB_PASSWORD='<LOCAL_APP_PASSWORD>' \
   APP_SEED_READER_USERNAME=clinic_viewer APP_SEED_READER_PASSWORD='<LOCAL_READER_PASSWORD>' \
   APP_SEED_MANAGER_USERNAME=clinic_manager APP_SEED_MANAGER_PASSWORD='<LOCAL_MANAGER_PASSWORD>' \
   $HOME/dev-tools/jdk-21.0.13+9/Contents/Home/bin/java -jar target/webdev2-0.0.1-SNAPSHOT.jar
   ```

3. Verify `/api/health` returns `{"database":"UP","status":"UP"}` and open `http://localhost:8080/`.

## 5. Troubleshooting
- `Schema-validation: missing table`: verify maintainer ran `01_schema.sql` against the target `DB_URL`.
- `Access denied for user 'vetclinic_app'@'localhost'`: check `GRANT` ran on the target DB after creating it.
- `403 Forbidden` on `POST`/`PUT`/`DELETE`: confirm login succeeded and `X-CSRF-TOKEN` came from `/api/auth/csrf` after login.
