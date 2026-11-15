# BookMyShow — Movie Ticket Booking REST API

This is a backend project I built to learn Spring Boot properly — a clone of BookMyShow's
core flow: browse movies and showtimes, pick seats, book a ticket. Built with **Spring Boot**,
**Spring Security (JWT)**, **Spring Data JPA** and **MySQL**.

I started this mainly to practice building a real REST API end-to-end (not just CRUD
tutorials) and to understand how authentication/authorization actually works under the
hood instead of copy-pasting a security config. It's also the project I'm using for
internship applications, so I've tried to keep it close to how a real small backend
would be structured.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.16 |
| Security | Spring Security 6.5 + JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 (H2 for tests) |
| Validation | Jakarta Bean Validation |
| Docs | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Boilerplate | Lombok |

---

## Getting Started

### Prerequisites
- JDK 21
- MySQL 8 running on `localhost:3306`
- Maven (or just use the bundled `./mvnw`, you don't need Maven installed separately)

### 1. Configure the database

You don't need to create the `BMS` database by hand — `createDatabaseIfNotExist=true`
in the connection URL does that for you on first run.

Set your MySQL password through an environment variable instead of hardcoding it:

```bash
# Windows PowerShell
$env:DB_PASSWORD = "your_mysql_password"

# macOS / Linux
export DB_PASSWORD=your_mysql_password
```

(Or just edit the default in `src/main/resources/application.properties` if you're
only running this locally for yourself.)

### 2. Run it

```bash
./mvnw spring-boot:run
```

Starts on **http://localhost:8080**.

### 3. Check out the API docs

**http://localhost:8080/swagger-ui.html**

I added Swagger mainly so I didn't have to keep a separate Postman collection around —
it reads the controllers directly, so it's always in sync with the actual code.

### 4. Log in as admin

An admin account gets created automatically the first time the app runs against an
empty database (see `AdminSeeder`):

```
email:    admin@bms.com
password: admin123
```

You can override these with `ADMIN_EMAIL` / `ADMIN_PASSWORD` env vars.

---

## Authentication

The API is **stateless** — no sessions, no cookies. Every protected request has to
carry a JWT.

```
1. POST /api/auth/register   ->  creates a USER account
2. POST /api/auth/login      ->  returns { token, type, email, role }
3. send the token on every later request:

   Authorization: Bearer <token>
```

Tokens are signed with **HS256** and expire after **24 hours**. Passwords are hashed
with **BCrypt** and never show up in any response (`@JsonIgnore` on the field).

### Using it in Swagger UI
Log in through `/api/auth/login`, copy the `token` from the response, click
**Authorize** (top right of the Swagger page), paste it in. After that every endpoint
call goes through as that user — saves you from manually adding the header every time.

---

## Roles

| Role | Can do |
|---|---|
| *(anonymous)* | Browse movies, shows, cities, theaters, screens, seats, check seat availability |
| `USER` | All of the above, plus book seats, view and cancel **their own** bookings |
| `ADMIN` | All of the above, plus manage the whole catalogue and all users |

Registration **always** creates a `USER` — the role is decided on the server, not read
from the request body, so there's no way to sign up and pass `"role": "ADMIN"` to make
yourself an admin (this actually worked in an earlier version of this project before I
fixed it — see the Engineering Notes below). New admins can only be created by an
existing admin, via `POST /api/users/admin`.

---

## API Endpoints

### Auth — `/api/auth`
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/register` | Public | Create a USER account |
| POST | `/login` | Public | Get a JWT |
| GET | `/me` | Authenticated | Your own profile, identified from the token |

### Movies — `/api/movies`
| Method | Endpoint | Access |
|---|---|---|
| GET | `/` | Public |
| GET | `/{movieId}` | Public |
| GET | `/genre/{genre}` | Public |
| GET | `/language/{language}` | Public |
| GET | `/search/{title}` | Public |
| POST | `/movie` | ADMIN |
| PUT | `/{movieId}` | ADMIN |
| DELETE | `/{movieId}` | ADMIN |

### Shows — `/api/shows`
| Method | Endpoint | Access |
|---|---|---|
| GET | `/` | Public |
| GET | `/movie/{movieId}` | Public |
| GET | `/movie/{movieId}/date?date=YYYY-MM-DD` | Public |
| GET | `/screen/{screenId}` | Public |
| GET | `/screen/{screenId}/date?date=YYYY-MM-DD` | Public |
| POST | `/show` | ADMIN |

### Booking — `/api/booking`
| Method | Endpoint | Access |
|---|---|---|
| GET | `/show/{showId}/available-seats` | Public |
| POST | `/` | USER |
| GET | `/my` | USER |
| GET | `/{bookingId}` | Owner or ADMIN |
| PUT | `/{bookingId}/cancel` | Owner or ADMIN |

### Catalogue — `/api/cities`, `/api/theaters`, `/api/screen`, `/api/seats`
All `GET` endpoints are public. All `POST`/`PUT`/`PATCH`/`DELETE` need `ADMIN`.

### Users — `/api/users` *(ADMIN only)*
| Method | Endpoint |
|---|---|
| GET | `/` |
| GET | `/{id}` |
| POST | `/admin` |

---

## Example: booking a ticket end-to-end

```bash
# 1. register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@test.com","password":"alice123","phone":"9999999999"}'

# 2. login, copy the token from the response
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@test.com","password":"alice123"}'

# 3. check which seats are free
curl http://localhost:8080/api/booking/show/1/available-seats

# 4. book - notice there's no userId in the body, it comes from the token
curl -X POST http://localhost:8080/api/booking \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"showId":1,"seatIds":[10,11]}'

# 5. check your bookings
curl http://localhost:8080/api/booking/my \
  -H "Authorization: Bearer <token>"
```

---

## Data Model

```
City  1───*  Theater  1───*  Screen  1───*  Seat
                                │              │
                                │              │ *
                                │ 1            │
                                *              │
Movie  1───*  Show ─────────────┘              │
                │                              │
                │ 1                            │
                *                              *
             Booking  *──────────────────── (booking_seats)
                │
                │ *
                1
              User
```

- A **City** has many **Theaters**; a Theater has many **Screens** (think of these as
  individual auditoriums); a Screen has many **Seats**.
- A **Show** is one **Movie** playing on one **Screen** at a specific date/time.
- A **Booking** belongs to one **User** and one **Show**, and covers one or more
  **Seats** (join table `booking_seats`).

**Enums:** `Role` (USER, ADMIN) · `BookingStatus` (CONFIRMED, CANCELLED) · `SeatType`
(REGULAR, PREMIUM, VIP)

---

## Project Structure

```
src/main/java/com/cfs/BookMyShow/
├── config/        SecurityConfig, OpenApiConfig, AdminSeeder, CorsConfig
├── controller/    REST endpoints
├── dto/           Request objects (so the client never talks to entities directly)
├── entity/        JPA entities
├── enums/         Role, BookingStatus, SeatType
├── exception/     Custom exceptions + GlobalExceptionHandler
├── repository/    Spring Data JPA repositories
├── security/      JwtUtil, JwtAuthFilter, 401/403 handlers
└── service/       Business logic
```

---

## Things I ran into while building this

A few bugs/decisions that actually taught me something, so I kept notes on them instead
of just quietly fixing and forgetting.

### 1. Two people could book the same seat at the same time

My first version just checked "is this seat already booked?" and then saved the
booking right after — two separate steps. Under load (I tested this by firing ~30
parallel requests at the same seat with a quick script) several requests all read
"seat is free" before any of them had actually saved anything, so the same seat got
booked more than once. This is a classic **check-then-act race condition**.

The fix:

```java
@Transactional                                    // keeps the lock held until commit
public Booking createBooking(BookingRequest r){
    Show show = showService.getShowByIdForUpdate(r.getShowId());   // SELECT ... FOR UPDATE
    
}
```

`@Lock(PESSIMISTIC_WRITE)` on the repository query makes other requests for the *same
show* wait in line. But that alone isn't enough — without `@Transactional`, the lock
is released the instant the query finishes, before the booking is even saved, and the
race comes right back. Both together is what actually fixes it.

Bookings for *different* shows aren't affected — they still run fully in parallel,
since the lock is per-show, not global. (If I ever took this further, the "real"
fix would probably be a `ShowSeat` table with a unique constraint on
`(show_id, seat_id)`, so the database itself refuses the duplicate.)

### 2. Never trust an ID the client sends you

`BookingRequest` originally had a `userId` field in the body. I realized while testing
that any logged-in user could just change that number and book a ticket "as" someone
else — nothing stopped it. I removed the field entirely; the user booking is now
whoever the JWT says is logged in, period. Same issue existed on
`GET /api/booking/user/{userId}`, which I replaced with `GET /api/booking/my` (no id
in the URL at all, so there's nothing left to tamper with).

Lesson I took from this: **the client can say what it wants to do, but never let it
say who it is.**

### 3. Role-check vs. ownership-check are two different things

`SecurityConfig` can say "you need to be ADMIN to hit this URL" — that's a URL-pattern
rule, easy. What it *can't* say is "this specific booking has to belong to you",
because that depends on the actual row in the database, not the URL. So that check
lives inside `BookingService` instead, and throws `AccessDeniedException` so it comes
back as a proper **403**, not a generic 400. Admins skip this check on purpose, since
support staff need to be able to look up and cancel bookings for other people.

### 4. Exception handling was way too generic at first

Every `throw new RuntimeException("...")` used to land in one catch-all handler that
always returned 400, no matter what actually went wrong — "movie not found" and
"email already registered" both came back as the same status code. I split this out
into proper exception types (`ResourceNotFoundException` → 404,
`DuplicateResourceException` / `SeatAlreadyBookedException` → 409,
`InvalidCredentialsException` → 401), each with its own handler in
`GlobalExceptionHandler`. Small change, but it's the difference between an API that's
actually usable by a frontend and one where every error looks the same.

---

## Testing



## Configuration Reference

| Property | Env var | Default |
|---|---|---|
| `spring.datasource.password` | `DB_PASSWORD` | — |
| `app.jwt.secret` | `JWT_SECRET` | dev default |
| `app.jwt.expiration-ms` | `JWT_EXPIRATION_MS` | `86400000` (24h) |
| `app.admin.email` | `ADMIN_EMAIL` | `admin@bms.com` |
| `app.admin.password` | `ADMIN_PASSWORD` | `admin123` |

> The JWT secret and admin password have dev defaults baked in so anyone cloning this
> repo can run it immediately without setting anything up first. Obviously if this
> were ever deployed somewhere real, those would need to be set through actual env
> vars instead — whoever knows the signing secret can forge a valid token for any
> user, including an admin.

---
