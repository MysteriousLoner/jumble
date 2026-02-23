# Jumble Word Game - Docker Setup

This project is fully dockerized with both client and server applications.

## Prerequisites

- Docker Desktop (includes Docker and Docker Compose)
- At least 2GB of free RAM for Docker

## Quick Start

From the root directory of the project, run:

```bash
docker-compose up
```

This will:
1. Build the Spring Boot server application (Java)
2. Build the React client application
3. Start both services

### Access the Application

- **Client (React App)**: http://localhost:3000
- **Server API**: http://localhost:8080/api/game/new
- **API Documentation**: http://localhost:8080/swagger-ui.html

## Docker Commands

### Start services (build if needed)
```bash
docker-compose up
```

### Start services in detached mode (background)
```bash
docker-compose up -d
```

### Stop services
```bash
docker-compose down
```

### Rebuild and start services
```bash
docker-compose up --build
```

### View logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs server
docker-compose logs client

# Follow logs in real-time
docker-compose logs -f
```

### Check running containers
```bash
docker-compose ps
```

## Architecture

### Server Container
- **Base Image**: Eclipse Temurin 8 JRE (Alpine)
- **Build**: Multi-stage build with Maven
- **Port**: 8080
- **Health Check**: Included

### Client Container
- **Base Image**: Nginx (Alpine)
- **Build**: Multi-stage build with Node.js
- **Port**: 80 (mapped to 3000 on host)
- **Routing**: Nginx handles SPA routing and proxies `/api/*` to server

### Network
Both containers communicate through a Docker bridge network named `jumble-network`.

## Development vs Production

### For Development (without Docker)
```bash
# Terminal 1 - Server
cd server
./mvnw spring-boot:run

# Terminal 2 - Client
cd client
npm install
npm start
```

### For Production (with Docker)
```bash
docker-compose up
```

## Troubleshooting

### Port Already in Use
If ports 3000 or 8080 are already in use, modify the ports in [docker-compose.yml](docker-compose.yml):
```yaml
ports:
  - "3001:80"  # Change 3000 to 3001 for client
  - "8081:8080"  # Change 8080 to 8081 for server
```

### Rebuild from Scratch
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up
```

### Remove All Containers and Images
```bash
docker-compose down --rmi all --volumes
```

## File Structure

```
jumble-ly/
├── docker-compose.yml          # Orchestrates both services
├── client/
│   ├── Dockerfile              # Client container definition
│   ├── .dockerignore          # Files to exclude from build
│   ├── nginx.conf             # Nginx configuration
│   └── ...
└── server/
    ├── Dockerfile              # Server container definition
    ├── .dockerignore          # Files to exclude from build
    └── ...
```
