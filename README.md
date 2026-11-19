cd C:\nginx

# Check config
.\nginx.exe -t

# Start
.\nginx.exe

# Reload config
.\nginx.exe -s reload

# JWT Cookie + SPA CSRF demo

## Admin app

After signing in, the left sidebar opens Products, Customers, and Delivery. Each section supports listing, creating, editing, and deleting records. Deliveries belong to customers; delete a customer's deliveries before deleting that customer. The sample data is stored in memory and resets when the backend restarts.

Admin URLs: `/admin/products`, `/admin/customers`, and `/admin/deliveries`. Opening `/` after sign-in goes to `/admin/products`.

The frontend uses React Router nested routes and a protected admin layout. Each page fetches its own data when opened. Delivery also fetches customers for its customer selector.

## Flow

- React calls `GET /api/auth/me` on startup to restore a session. This request goes through the SPA CSRF handler and can issue `XSRF-TOKEN` even when the response is 401.
- `POST /api/auth/login` is **CSRF-exempt** via `ignoringRequestMatchers`, not merely `permitAll`. Its request passes through `SpaCsrfTokenRequestHandler`, which calls `csrfToken.get()` and issues `Set-Cookie: XSRF-TOKEN=...` when absent. On successful login, the custom controller also sets `ACCESS_TOKEN` (HttpOnly).
- `POST /api/auth/logout` is also CSRF-exempt in this demo. The custom controller clears the JWT cookie.
- Axios reads `XSRF-TOKEN` and sends `X-XSRF-TOKEN` on subsequent state-changing requests. `POST/PUT/DELETE /api/products` are CSRF-protected.
- No `/api/health`, `/api/csrf`, or explicit `bootstrapCsrf()` request is used.
- This demo uses custom login/logout controllers, not Spring Security's standard authentication/logout filters; CSRF token rotation is not automatically performed by those controllers.

## Run

Backend: `cd backend && mvn spring-boot:run` (Java 17+).

Frontend: `cd frontend && npm install && npm run dev`.

Demo credentials: `demo` / `demo123`.

For local Nginx routing, configure `fe.test` and `be.test` to resolve to `127.0.0.1`, and run the supplied `nginx/nginx.conf`. Open `http://fe.test`. The frontend uses relative `/api` requests.

Local HTTP uses `app.cookie-secure: false` for testing only. Enable `Secure` and HTTPS in production.

## Security notes

Exempting login/logout from CSRF is a conscious demo choice, not Spring Security's recommended default. Login CSRF and logout CSRF remain potential concerns. `SameSite=Strict` helps but is not a complete substitute for CSRF validation.

The SPA handler forces deferred token loading, but it cannot make an initial unsafe request without a CSRF header pass validation. The first product mutation must follow a response that established the XSRF cookie.

Debug token logs must never be enabled in production. JWT cookies are HttpOnly and cannot be read by frontend JavaScript.
