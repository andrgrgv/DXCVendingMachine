# DXC Vending Machine

Responsive vending machine application with a Spring Boot backend and Vue frontend.

## Structure

```text
dxc-vending-machine/
  README.md
  .gitignore
  compose.yaml
  backend/
    Dockerfile
    build.gradle
    src/
  frontend/
    Dockerfile
    package.json
    src/
```

## Run With Docker Compose

From this directory:

```bash
docker compose up --build
```

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8080
```

## Run Tests

Backend tests:

```bash
cd backend
./gradlew test
```

On Windows:

```powershell
cd backend
.\gradlew test
```

Frontend type-check and production build:

```bash
cd frontend
npm install
npm run build
```

## Currency

The application uses EUR and accepts these coin denominations:

- EUR 0.01
- EUR 0.02
- EUR 0.05
- EUR 0.10
- EUR 0.20
- EUR 0.50
- EUR 1.00
- EUR 2.00

## Notes

### Frontend Design

The frontend is built with **Vue 3**, **TypeScript**, **Vite**, **Vue Router**, and **Pinia**.

The application is structured as a single-page application with two views:

* **Vending Machine** – customer interface for inserting coins and purchasing products.
* **Admin Panel** – interface for managing products through CRUD operations.

The UI is composed of reusable components (`ProductGrid`, `ProductCard`, `PaymentPanel`, and `MessageBox`) to keep presentation logic modular and maintainable.

A dedicated API service centralizes all communication with the Spring Boot backend. The backend remains the source of truth for product inventory and vending sessions, while Pinia stores the current vending session state (inserted amount, loading state, errors, and purchase results) so it can be shared across components without prop drilling.

The layout is responsive and adapts to desktop and mobile screen sizes using CSS Grid and media queries.

### Backend Design

The backend is implemented with **Java** and **Spring Boot** and exposes a REST API for product management and vending operations.

The application follows a simple layered architecture:

* **Controllers** expose REST endpoints and handle HTTP communication.
* **Services** contain the product and vending business logic.
* **Repositories** manage the in-memory product inventory.
* **DTOs** define the data exchanged through the API.

Products are stored in application memory using a thread-safe `ConcurrentHashMap` and are initially populated from a mocked external product API. No database is used, as persistence beyond the application lifecycle is not required.

Each client has an independent vending session implemented using Spring's `@SessionScope`. The session tracks inserted coins, while the product inventory is shared between all users.

Monetary values are represented using `BigDecimal` to avoid floating-point precision issues. Accepted Euro denominations are represented by a `Coin` enum, and change is calculated using the available denominations.

Validation and centralized exception handling are used to return consistent HTTP error responses.

### Further Notes

Product administration is available through an admin-style frontend view at `/admin` and REST API at `/api/products`. Authentication and authorization are not implemented because they were not part of the assignment requirements. In a production system, product CRUD operations should be protected with admin-only access. Adding a link to the admin panel from the vending panel was deliberately skipped, as it should not be accessible by the standard user facing the machine. 

Initial products are loaded from a mocked external product client in the backend. Product CRUD changes are stored only in application memory.
