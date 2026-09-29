# URL Shortener & Analytics API

A production-ready URL shortener built with Spring Boot, utilizing a PostgreSQL database for persistence and a Redis cache-aside layer for high-performance redirection.

## Tech Stack
* **Java / Spring Boot** (Core framework)
* **PostgreSQL** (Relational data & Base62 ID generation)
* **Redis** (Sub-millisecond read caching & click counters)
* **Docker** (Containerized infrastructure)
* **Maven** (Dependency management)

## Prerequisites
* Java 21+
* Docker Desktop
* Maven

## Quick Start

1. **Start the Infrastructure (PostgreSQL & Redis):**
   ```bash
   docker compose up -d
   ```

2. **Run the Application:**
   Run `UrlShortenerApplication.java` in your IDE or use Maven:
   ```bash
   mvn spring-boot:run
   ```
   *(The API will start on http://localhost:8080)*

## API Endpoints

### 1. Shorten a URL
* **POST** `/api/v1/urls`
* **Content-Type:** `application/json`
* **Body:**
  ```json
  {
    "originalUrl": "[https://github.com](https://github.com)"
  }
  ```

### 2. Redirect
* **GET** `/{shortCode}`
* **Action:** Issues an HTTP 302 redirect to the original URL. Automatically tracks the click and caches the result in Redis.

### 3. View Analytics
* **GET** `/api/v1/urls/{shortCode}/analytics`
* **Action:** Returns total clicks and the 10 most recent click events (timestamp, user-agent, referer).
