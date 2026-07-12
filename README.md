# 🏬 Ecommerce Platform — Backend

A Spring Boot backend for an e-commerce application, built as a **modular monolith** with clear domain-driven package boundaries (auth, product, order, payment) — structured so any module could be extracted into an independent microservice down the line.

> Built by [Priyansh Gautam](https://github.com/priyansh-21)

**🔗 Live API:** [https://ecommerce-platform-70zn.onrender.com](https://ecommerce-platform-70zn.onrender.com)
**🔗 Live Frontend:** [Stockroom (React)](https://github.com/priyansh-21/ecommerce-frontend-platform) — try the full app here

> ⚠️ **Note:** This is deployed on Render's free tier, which spins down after periods of inactivity. The **first request may take 30–60 seconds** to respond while the server wakes up — subsequent requests are fast.

---

## Features

- **JWT-based authentication** — register/login with role-based access (customer vs. admin)
- **Product catalog** — full CRUD for admins, public read access for customers, with Redis-ready caching support
- **Order lifecycle** — cart-to-order flow with stock-aware validation
- **Payment processing** — simulated payment flow (no real payment gateway is integrated; this is a demo project) with multi-currency support
- **Health monitoring** — Spring Boot Actuator endpoint for uptime/status checks
- **Centralized exception handling** — consistent error responses across all endpoints

## Architecture
