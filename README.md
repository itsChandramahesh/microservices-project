# Microservices Training Workspace

This workspace contains 7 small applications for DevOps training:

- `user-service` - Spring Boot service on port 8081
- `product-service` - Spring Boot service on port 8082
- `order-service` - Spring Boot service on port 8083
- `payment-service` - Flask service on port 5001
- `notification-service` - Flask service on port 5002
- `analytics-service` - Flask service on port 5003
- `order-frontend` - React app that calls the order service

## Quick Start

### Spring Boot services

Run each service from its own folder:

```powershell
mvn spring-boot:run
```

### Python services

Install dependencies and start each app:

```powershell
pip install -r requirements.txt
python app.py
```

### React app

```powershell
npm install
npm run dev
```

## Notes

The order service is configured for service-to-service calls using hostnames like `user-service` and also includes localhost fallbacks for simple local training runs.

The frontend calls `/api/order` by default. In local Vite development, that path is proxied to `http://localhost:8083`. In the containerized frontend, Nginx proxies `/api/order` to the Kubernetes or Docker service name `order-service:8083`.

If you want a custom frontend backend target at build time, set `VITE_ORDER_API_URL`.
