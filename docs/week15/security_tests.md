# WEBDEV2 Week 15 Security Test Matrix (F3)

All API tests have been executed on `webdev2` (`week15`) using MySQL database version `8.4.13`. The connection was made as user `vetclinic_app@localhost`, and `ddl-auto` was set to `validate`. Token, cookie, and password values have been censored.

| # | Scenario | Identity / Role | Valid CSRF Context? | Request | Expected | Actual Observed Result | Persistence / UI Check |
|---|---|---|---|---|---|---|---|
| 1 | Anonymous protected read denied | Anonymous | N/A (`GET`) | `GET /api/pets` | `401` | `401` `{"status":401,"message":"Authentication required"}` | Login panel shown; workspace hidden (`[hidden]`) |
| 2 | Invalid password denied | `clinic_viewer` (bad password) | Yes (`GET /api/auth/csrf`) | `POST /api/auth/login` | `401` | `401` `{"status":401,"message":"Invalid username or password"}` | `#auth-status` displays `"Invalid username or password"` |
| 3 | Valid login succeeds | `clinic_viewer` (`READER`) | Yes | `POST /api/auth/login` | `200`, no hash returned | `200` `{"username":"clinic_viewer","role":"READER"}` | CSRF rotated; workspace visible, write forms hidden |
| 4 | Permitted role reads | `clinic_viewer` (`READER`) | N/A (`GET`) | `GET /api/pets?page=0&size=2` | `200` | `200` paged JSON (`Buddy`, `Luna`) | Pet List, Owners, and Appointments tables populated |
| 5 | Read-only role cannot mutate | `clinic_viewer` (`READER`) | **Yes** (fresh post-login CSRF) | `POST /api/pets` (`MC-BLK1`) | `403` | `403` `{"status":403,"message":"Forbidden: insufficient role or invalid CSRF token"}` | `pet` count unchanged in `Week15SecurityTests` & MySQL |
| 6 | Missing CSRF token denied | `clinic_manager` (`MANAGER`) | **No** (omitted) | `POST /api/pets` (`MC-BLK2`) | `403` | `403` `Forbidden: insufficient role or invalid CSRF token` | Denied by `CsrfFilter` before controller; `pet` count unchanged |
| 7 | Authorized manager mutates | `clinic_manager` (`MANAGER`) | Yes (fresh rotated CSRF) | `POST /api/pets` (`MC-SEC900`) & `DELETE` | `201` then `204` | `201` (`id=25`), then `204` | Browser test also created & reloaded `"Sec UI Owner"` then deleted (`204`) |
| 8 | Logout invalidates session | `clinic_manager` -> signed out | Yes on logout | `POST /api/auth/logout`, then `GET /api/pets` | `204`, then `401` | `204`, then `401` `Authentication required` | `#clinic-workspace` and `#user-panel` hidden, tables cleared |

Evidence sources: [evidence/security_curl_output.txt](evidence/security_curl_output.txt) and [evidence/maven_test_output.txt](evidence/maven_test_output.txt).
