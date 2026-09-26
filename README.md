# ProximityEats

**🚀 [Live Demo](https://proximityeats.onrender.com/login)** &nbsp;|&nbsp; **📘 [Swagger API Docs](https://proximityeats.onrender.com/swagger-ui.html)**

**A hyperlocal, society-aware food delivery backend — where neighbours share a cart, weather shrinks the delivery map, and the kitchen ships one bag instead of five.**

Built with Spring Boot 4, Spring Security (JWT + OAuth2), PostgreSQL,  Kafka, WebSocket/STOMP, and Razorpay.

---

## The Problem

Most food-delivery backends are built as if every order exists in isolation — one user, one cart, one delivery boy, one trip, and a delivery radius that never changes. In a dense housing society or hostel block, and under real-world conditions, that model breaks down in four predictable ways:

1. **Wasted trips.** Five people in the same society order from the same restaurant within minutes of each other, and the platform still dispatches five separate deliveries — five delivery fees, five delivery partners, five plates of idle capacity.
2. **Coordination tax on group orders.** When a group of friends or flatmates wants to order together, someone ends up collecting cash, chasing "did you add your item yet?", and manually placing one combined order. There's no shared cart with per-person payment splitting built into the backend.
3. **Static, unreliable restaurant availability.** Restaurants are shown to users as simply "open" or "closed" based on their own toggle, with no regard for whether a delivery partner is actually reachable nearby — leading to accepted orders that no one can pick up.
4. **A fixed delivery radius, regardless of conditions.** A menu item's "deliverable within X km" limit stays the same whether it's a clear afternoon or a thunderstorm — so orders get accepted into conditions no delivery partner can realistically complete, and menus/subscriptions have no automated lifecycle for price/availability changes or recurring billing either.

ProximityEats is a backend designed around the reality of **shared geography and shifting conditions** — the same restaurant, the same society, the same delivery radius, the same weather — and automates the batching, coordination, and lifecycle management that a naive single-user ordering system leaves to chance.

---

## Key Features

### 🛒 Join Cart (Group Ordering)
A real, first-class group-cart flow — not a workaround built on top of a personal cart:
- Any user can spin up a group cart and share a join code/link; others join, add their own items, and see the cart update live.
- Each participant is billed individually (`GroupPay`) rather than one person fronting the bill, with per-user payment confirmation tracked before the group order is placed.
- A scheduled reaper automatically expires stale group carts, locks a cart once payment starts, and confirms the order once every participant has paid — no manual intervention needed.

### 📍 Delivery-Partner-Aware Restaurant Availability
Restaurant availability isn't a manual toggle — it's computed:
- A scheduled job periodically runs a **Dijkstra shortest-path** calculation across a society/zone graph to find the nearest available delivery partner to each restaurant.
- If no delivery partner is within a defined distance threshold, the restaurant is automatically marked `UNAVAILABLE`; otherwise it's flagged `AVAILABLE` again — so users never browse a restaurant they can't actually get food from.

### 🌧️ Weather-Aware Dynamic Delivery Radius
Every menu item carries a maximum deliverable radius, and that radius isn't fixed — it reacts to live weather:
- Before showing a restaurant's menu, the backend calls a live weather API for the restaurant's town/city and checks the current conditions.
- If the conditions indicate rain, drizzle, thunder, or storm, each item's `max_radius_km` is automatically shrunk (reduced by a fraction of its normal range) for that request.
- The user's actual distance from the restaurant is then checked against this — potentially shrunk — radius: items within range are marked `AVAILABLE`, and items now outside the reduced radius are marked `HIDE`, so bad weather quietly narrows what's orderable instead of letting a delivery partner get sent out on an unrealistic trip.

### 📦 Automatic Order Bundling (Same Restaurant + Same Society)
When multiple independent orders land on the same restaurant from the same society/zone within a short window, the backend automatically clusters them into a single deliverable **Bundle**:
- Orders in `PREPARING` status are grouped by restaurant and by society zone, and either added to an existing live bundle or used to create a new one.
- A single delivery partner picks up and delivers the whole bundle, splitting the delivery earnings/fee logic accordingly, while orders that get cancelled mid-flight are cleanly removed from the bundle and its price recalculated.
- Stale or abandoned bundles auto-cancel, and completed bundles auto-credit the assigned delivery partner's wallet.

### 🍽️ Dynamic Menu
Menu items aren't static rows — price and availability are designed to be updated in real time by restaurant managers (price updates, item add/remove, stock/availability flags), so what a user sees reflects the kitchen's current state rather than a snapshot taken at onboarding.

### 🔁 Automated Membership / Subscription Lifecycle
Subscriptions don't rely on a human (or the user) to remember to renew:
- A daily job finds subscriptions due for renewal and **auto-charges** the saved payment method through Razorpay, without any manual "renew now" click.
- A separate reminder job emails users a few days before their next billing date.
- Idempotency keys guard the checkout flow so a duplicate subscribe request (double-click, retry, flaky network) never creates two active subscriptions.

### 👤 Role-Based Access & Auth
JWT-based authentication with refresh tokens, Google OAuth2 login, and role-based authorization (`USER`, `MANAGER`, `DELIVERY_BOY`, `ADMIN`) enforced with `@PreAuthorize` across every controller.

### 💳 Payments & Wallet
Razorpay integration for order and subscription payments, webhook handling with replay protection (`ProcessedWebhook`), and an internal wallet system used both for user refunds and for crediting delivery partners on completed deliveries/bundles.

### 📣 Async Notifications
Kafka-backed email pipeline (producer/consumer) for transactional and reminder emails, decoupling email delivery from the request thread.

### 🖼️ Media Uploads
Cloudinary integration for restaurant, menu, and profile image uploads.

### 🛠️ Admin & Manager Tooling
Dedicated `AdminController` and `ManagerController` for restaurant onboarding, restaurant-profile management, and platform oversight, plus structured audit logging (`AuditLog`) for sensitive actions.

### 🧪 Testing & Docs
Integration tests using Testcontainers (Postgres), and an OpenAPI/Swagger UI (`springdoc-openapi`) for interactive API exploration.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language / Framework | Java, Spring Boot 4 |
| Security | Spring Security, JWT (`jjwt`), OAuth2 (Google) |
| Persistence | PostgreSQL, Spring Data JPA / Hibernate |
| Messaging | Apache Kafka |
| Real-time | Spring WebSocket / STOMP |
| Payments | Razorpay |
| Weather | WeatherAPI (live conditions lookup) |
| Media | Cloudinary |
| Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit, Spring Boot Test, Testcontainers |
| Build | Maven |

---

## Getting Started

### Prerequisites
- Java 21+ (matching your Spring Boot 4 setup)
- Maven
- PostgreSQL
- Redis
- Apache Kafka
- A Razorpay test account (for payments)
- A WeatherAPI account (for the dynamic radius feature)
- A Cloudinary account (for image uploads)
- A Google OAuth2 client ID/secret (for social login)

### Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/postgres
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_APP_PASSWORD}

RAZOR_PAY_KEY=${RAZORPAY_KEY_ID}
RAZOR_PAY_SECRET=${RAZORPAY_KEY_SECRET}

CLOUDINARY_CLOUD_NAME=${CLOUDINARY_CLOUD_NAME}
CLOUDINARY_API_KEY=${CLOUDINARY_API_KEY}
CLOUDINARY_API_SECRET=${CLOUDINARY_API_SECRET}

api_Url=${WEATHER_API_URL}
api_key=${WEATHER_API_KEY}

spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

### Run locally

```bash
# Clone the repo
git clone https://github.com/<your-username>/proximity-eats.git
cd proximity-eats

# Set environment variables (or use a .env loader / IDE run config)
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
# ...and the rest of the variables above

# Build and run
mvn clean install
mvn spring-boot:run
```

The API will start on `http://localhost:8182` (configurable via `server.port`).

Once running, explore the API via Swagger UI at:
```
http://localhost:8182/swagger-ui.html
```

### Docker

A `Dockerfile` is included for containerized deployment:

```bash
docker build -t proximity-eats .
docker run -p 8182:8182 --env-file .env proximity-eats
```

---

## Project Structure

```
src/main/java/com/.../
├── Controller/     # REST endpoints (auth, cart, group cart, bundle, order, subscription, payment, delivery, admin, manager...)
├── Service/        # Business logic (matching the controllers above, plus TimerService for all scheduled jobs)
├── Entity/         # JPA entities
├── DTO/            # Request/response payloads
├── Repository/     # Spring Data JPA repositories
├── Handler/        # WebSocket, JWT filter, mail, login success/failure handlers
├── ExceptionHandler/  # Centralized error handling
├── Exception/      # Custom exception types
├── Config/         # Security, caching, WebSocket, bean configuration
└── Utilis/         # Enums (order/payment/subscription/bundle status, etc.)
```

---

## License

This project is available for educational and portfolio purposes. Add a license file (MIT recommended) before accepting external contributions.
