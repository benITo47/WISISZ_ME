# ✨ wisisz.me

A modern web app for managing group expenses, built with React (Vite) and Spring Boot.

---

## 🪽 Features

- Track who owes whom
- Add and split expenses in real-time
- Secure JWT-based authentication
- Fully dockerized for deployment

---

## ⚙️ Tech Stack

- **Frontend:** React + TypeScript + Vite
- **Backend:** Spring Boot (Java 21+)
- **Database:** PostgreSQL (Neon)
- **Deployment:** Docker & Nginx (Reverse Proxy)

---

## 🚀 Getting Started

### Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

Or with Docker:

```bash
docker build -t wisisz-backend ./backend
docker run -p 9229:8080 --env-file ./backend/.env_dla_backendu wisisz-backend
```

---

### Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
```

Or with Docker:

```bash
docker build -t wisisz-frontend ./frontend
docker run -p 9119:5173 wisisz-frontend
```

---

## 🔗 API Access

Make sure your frontend talks to this endpoint using:

```env
VITE_API_URL= api_url
```

## 🌐 Live Application

You can try the running app at:

```
https://wisiszme.benito.dev
```

Dummy login credentials:

```
Email: terry.jones@gmail.com
Password: Passw0rd
```
