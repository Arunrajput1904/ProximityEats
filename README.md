# 🍔 Proximal

🚀 [Live Demo](#) | 📘 [Swagger API Docs](#)

A hyper-local, community-optimized food delivery engine designed for high-density living.

Proximal intelligently clusters neighborhood orders, dynamically adapts delivery zones to live weather, and enables multi-payer shared carts—secured by a robust JWT and OAuth2 architecture.

---

## 🛑 The Problem

Traditional food-delivery backends treat every order as an isolated event—one user, one cart, one delivery trip—with static rules that ignore the realities of physical geography and live conditions. In dense housing societies, campuses, or office parks, this model creates predictable friction:

- **Logistical Redundancy:** Five neighbors order from the same restaurant within ten minutes, triggering five separate delivery fees, five drivers, and immense wasted capacity.
- **The "Shared Cart" Friction:** Group ordering usually means one person fronts the bill and chases down Venmo payments. Native backend support for a multi-user, multi-payer cart is completely missing.
- **Ghost Availability:** Restaurants toggle themselves "open" without knowing if a delivery partner is actually nearby, leading to accepted orders that sit indefinitely on the counter.
- **Static Radii vs. Dynamic Weather:** A 10km delivery radius makes sense on a sunny day, but causes catastrophic delays during a thunderstorm. Menus don't automatically adapt to changing physical realities.

> **💡 The Solution:** Proximal solves this by treating the backend as a spatially aware, dynamic ecosystem. It automates batching, strictly manages real-world availability, and secures the entire pipeline from group-cart to gateway.

---

## ✨ Key Features

### 🔒 Robust Security & Authentication

Security is built into the foundation of the platform to protect user data, financial transactions, and API integrity:

- **JWT & Stateless Sessions:** Secure, scalable authentication using signed JSON Web Tokens (`jjwt`) with a dedicated refresh-token lifecycle for seamless, secure user sessions.
- **OAuth2 SSO:** Frictionless and secure Google Login integration managed via Spring Security.
- **Role-Based Access Control (RBAC):** Strict endpoint protection using `@PreAuthorize` to guarantee separation of concerns across `USER`, `MANAGER`, `DELIVERY_BOY`, and `ADMIN` roles.
- **Financial Integrity:** Razorpay webhook payloads are authenticated via signature verification (`ProcessedWebhook`) to prevent payload spoofing. Idempotency keys are enforced during checkout to eliminate duplicate charges on network retries.
- **Audit Trails:** Sensitive administrative and managerial actions are permanently recorded in an `AuditLog` for platform oversight.

### 🛒 Collaborative Carts (GroupPay)

A native, first-class group ordering flow:

- A user generates a secure join code/link to initialize a shared cart.
- Participants join, add items, and watch the cart sync in real-time via WebSocket/STOMP.
- **Split Billing:** Each participant checks out independently. The backend locks the cart when payment initiates and only dispatches the order to the restaurant once every user has completed their individual Razorpay transaction. Stale carts are automatically reaped.

### 📦 Intelligent Order Clustering (The Bundle)

When multiple independent orders hit the same restaurant from the same geographic zone within a short timeframe, the backend intervenes:

- Orders in `PREPARING` status are clustered by restaurant and destination zone into a single Bundle.
- One delivery partner is dispatched for the entire cluster. Delivery fees and partner payouts are dynamically recalculated, saving trips and carbon while maximizing partner earnings per hour.

### 🌧️ Weather-Reactive Delivery Zones

Delivery limits aren't static—they react to the sky:

- Before rendering a menu, the backend queries a live WeatherAPI for the restaurant's coordinates.
- If conditions are adverse (rain, thunderstorm, low visibility), the backend applies a fractional reduction to the `max_radius_km` of every menu item.
- Users outside this dynamically shrunken radius will see items seamlessly transition to `HIDE` or `UNAVAILABLE`, preventing drivers from accepting dangerous or impossible routes.

### 📍 Partner-Aware Restaurant Availability

Restaurants aren't just "Open"; they are "Reachable":

- A CRON job runs a Dijkstra shortest-path algorithm across the local zone graph to map driver proximity to restaurants.
- If no delivery partner is within an acceptable threshold, the restaurant is automatically toggled to `UNAVAILABLE` until the logistics network recovers.

### 🔁 Automated Subscription Lifecycle

Platform memberships require zero manual intervention:

- Daily scheduled tasks detect expiring subscriptions and auto-charge the saved payment method via Razorpay.
- Kafka-driven email pipelines notify users days before upcoming charges.

### 📣 Async Notifications & Media

- **Kafka Pipelines:** Transactional emails, receipts, and alerts are decoupled from the main thread via an Apache Kafka producer/consumer model.
- **Cloud Delivery:** Restaurant, menu, and user media are securely uploaded and served via Cloudinary integration.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Java, Spring Boot 4 |
| Security | Spring Security, JWT (`jjwt`), OAuth2 (Google) |
| Persistence | PostgreSQL, Spring Data JPA / Hibernate |
| Messaging | Apache Kafka |
| Real-time | Spring WebSocket / STOMP |
| Payments | Razorpay |
| External APIs | WeatherAPI (Live Conditions), Cloudinary (Media) |
| Docs & Testing | springdoc-openapi (Swagger), JUnit, Testcontainers |
| Build Tool | Maven |

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven
- PostgreSQL & Redis
- Apache Kafka
- API Keys: Razorpay (Test Mode), WeatherAPI, Cloudinary, Google OAuth2

### Environment Configuration

Create a `.env` file in the root directory or export these variables in your terminal:

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

# JWT Secret Key (Minimum 256-bit for HS256)
JWT_SECRET=${JWT_SECRET_KEY}

spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
```

### Local Development

```bash
# Clone the repository
git clone https://github.com/<your-username>/blocbites.git
cd blocbites

# Build the application
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

The server initializes on `http://localhost:8182`.

Access the interactive API documentation at: `http://localhost:8182/swagger-ui.html`

### Docker Deployment

```bash
# Build the image
docker build -t blocbites-backend .

# Run the container with injected environment variables
docker run -p 8182:8182 --env-file .env blocbites-backend
```

---

## 🏗️ Architecture & Structure

```text
src/main/java/com/.../blocbites/
├── Controller/         # REST endpoints secured via @PreAuthorize (Auth, Carts, Bundles, Admin)
├── Service/            # Business logic, CRON jobs, Dijkstra routing, Weather evaluation
├── Security/           # JWT Filters, OAuth2 Handlers, Authentication Providers
├── Entity/             # JPA Entities mapped to PostgreSQL
├── DTO/                # Data Transfer Objects for strict payload validation
├── Repository/         # Spring Data JPA interfaces
├── Handler/            # WebSocket configuration, Email dispatch, Exception handling
├── Config/             # CORS, SecurityFilterChain, Kafka, and Redis configurations
└── Utils/              # Enums, Constants, and Helper classes
```

---

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details.
