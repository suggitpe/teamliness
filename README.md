# Teamliness — Organisational Team & Outcome Management

**Teamliness** is an enterprise Organisational Team & Outcome Management platform designed for engineering and operational leaders. Built with a **Hexagonal / Modular Monolith Backend** (Java 21 + Spring Boot 3 + PostgreSQL 16 + Flyway) and a **Glassmorphic Single-Page Application (SPA) Frontend** (React 18 + TypeScript + Vite + Tailwind CSS), Teamliness bridges:

* **Workday System-of-Record Employee Directory (`WorkdayDirectoryPort`):** Idempotent directory synchronization (`MockWorkdayAdapter` & `WorkdayRaaSAdapter`) with read-only HR projections and automatic **Leaver Soft-Deactivation (`Inactive`)**.
* **Validated Skills & Team-Level Skill Rollups:** Standardized role & skill catalogs (`Beginner`, `Intermediate`, `Expert`) with aggregated **Team Skill Summary & Category Maturity Heatmaps**.
* **Hierarchical Org Units, Team Composition & Capacity Rules:** Multi-tier Org Unit trees (`Division -> Tribe/Department -> Team`), time-bounded memberships, and **Soft Over-Allocation Warnings (`>100%`)** + **Multi-Team Fragmentation (`>2` Teams)** alerts.
* **Outcome Commitments & Canonical Jira Integration:** High-level measurable Outcomes aligned to Teams (for core capability delivery or intentional skill growth), deep-linked to Jira Projects/Epics/JQL, and exposing a read-only **Canonical Jira Team Directory API** (`GET /api/v1/integrations/jira/teams`).
* **Organisational & Capacity Health Diagnostics Suite:** Real-time diagnostics (`GET /api/v1/diagnostics`) identifying `0%` unallocated / `<100%` under-allocated employees, `>100%` over-allocated / fragmented staff, active Workday leavers on teams, leadless teams, and outcome coverage gaps.
* **Specification by Example (ATDD) HTML Living Documentation:** Executable Gherkin `.feature` specifications generating human-readable HTML Living Documentation reports on every build.

---

## Table of Contents
1. [Prerequisites](#1-prerequisites)
2. [Running Locally for Development & Manual Review](#2-running-locally-for-development--manual-review)
3. [Running Automated Tests & Viewing HTML Living Documentation](#3-running-automated-tests--viewing-html-living-documentation)
4. [Deploying on a Dedicated Server (VM / Bare-Metal)](#4-deploying-on-a-dedicated-server-vm--bare-metal)
5. [Deploying as Containers in the Cloud (Google Cloud Platform — Cloud Run & Cloud SQL)](#5-deploying-as-containers-in-the-cloud-google-cloud-platform--cloud-run--cloud-sql)
6. [Continuous Integration & Deployment (GitHub Actions & Cloud Build)](#6-continuous-integration--deployment-github-actions--cloud-build)

---

## 1. Prerequisites

| Tool | Minimum Version | Notes |
| :--- | :--- | :--- |
| **Java JDK** | **Java 21 LTS** | OpenJDK / Eclipse Temurin / Azul Zulu 21 |
| **Node.js & npm** | **Node 20+ / 22+ LTS** | Includes `npm` for Vite frontend build and Vitest runner |
| **Docker** *(Optional locally)* | Docker 24+ / Compose v2 | Optional for local PostgreSQL 16; backend runs out-of-the-box with embedded PostgreSQL-compatibility mode if Docker is absent |

---

## 2. Running Locally for Development & Manual Review

### Option A: Zero-Dependency Local Run (No Docker Required)
By default, `backend/src/main/resources/application.yml` is pre-configured with an embedded PostgreSQL-compatible datasource fallback (`jdbc:h2:mem:teamliness;MODE=PostgreSQL`) that automatically executes all **Flyway** schema and seed migrations (`V1__init_schema.sql`, `V2__seed_local_data.sql`) on startup.

#### Step 1 — Start the Spring Boot Backend (`http://localhost:8080`)
Open a terminal in the repository root and run:

```powershell
cd backend
.\gradlew.bat bootRun
```
*(On Linux/macOS: `cd backend && ./gradlew bootRun`)*

Verify the backend endpoints once started:
* **System Status API:** [http://localhost:8080/api/v1/system/status](http://localhost:8080/api/v1/system/status)
* **OpenAPI / Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **Actuator Health Probe:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

#### Step 2 — Start the Glassmorphic React Frontend (`http://localhost:5173`)
Open a second terminal and run:

```powershell
cd frontend
npm install
npm run dev
```

Open **[http://localhost:5173](http://localhost:5173)** in your browser. Vite automatically proxies `/api/*` requests to `http://localhost:8080`.

---

### Option B: Local Run with PostgreSQL 16 via Docker Compose
If Docker is installed and running, you can run against a dedicated PostgreSQL 16 container:

```bash
# 1. Start PostgreSQL 16 container on port 5432
docker compose up -d

# 2. Start Backend pointed at local PostgreSQL 16
cd backend
SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/teamliness" \
SPRING_DATASOURCE_USERNAME="teamliness" \
SPRING_DATASOURCE_PASSWORD="teamliness_dev_password" \
./gradlew bootRun
```

On Windows PowerShell:
```powershell
docker compose up -d
cd backend
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/teamliness"
$env:SPRING_DATASOURCE_USERNAME="teamliness"
$env:SPRING_DATASOURCE_PASSWORD="teamliness_dev_password"
.\gradlew.bat bootRun
```

---

## 3. Running Automated Tests & Viewing HTML Living Documentation

All test suites run strictly in **single-run mode** (no interactive watch flags) and automatically compile executable Gherkin `.feature` specifications into **HTML Living Documentation**.

### Backend Unit + Cucumber ATDD Suite & JaCoCo Coverage
```powershell
cd backend
.\gradlew.bat test jacocoTestReport
```
* **Backend HTML Living Documentation Report:** `backend/build/reports/living-docs/index.html`
* **Backend JaCoCo Coverage Report:** `backend/build/reports/jacoco/test/html/index.html`

### Frontend TypeScript Check, Vitest ATDD Suite & v8 Coverage
```powershell
cd frontend
npm run lint
npm test -- --coverage
```
* **Frontend HTML Living Documentation Report:** `frontend/reports/living-docs/index.html` (also accessible at `http://localhost:5173/reports/living-docs/index.html` while `npm run dev` is running)
* **Frontend Coverage Report:** `frontend/coverage/index.html`

---

## 4. Deploying on a Dedicated Server (VM / Bare-Metal)

To deploy Teamliness on a dedicated Linux server (e.g., Ubuntu 24.04 LTS / RHEL 9) with **PostgreSQL 16**, **Systemd**, and **Nginx**:

### 4.1 Provision PostgreSQL 16 Database
```sql
CREATE USER teamliness WITH ENCRYPTED PASSWORD 'CHANGE_ME_STRONG_PASSWORD';
CREATE DATABASE teamliness OWNER teamliness;
```

### 4.2 Build & Configure the Backend Service (`systemd`)
1. Build the production executable JAR:
   ```bash
   cd backend
   ./gradlew bootJar --no-daemon
   sudo mkdir -p /opt/teamliness/backend
   sudo cp build/libs/teamliness-backend-0.1.0-SNAPSHOT.jar /opt/teamliness/backend/teamliness-backend.jar
   ```
2. Create `/etc/systemd/system/teamliness-backend.service`:
   ```ini
   [Unit]
   Description=Teamliness Spring Boot Backend Service
   After=network.target postgresql.service

   [Service]
   User=www-data
   WorkingDirectory=/opt/teamliness/backend
   Environment="SERVER_PORT=8080"
   Environment="SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5432/teamliness"
   Environment="SPRING_DATASOURCE_USERNAME=teamliness"
   Environment="SPRING_DATASOURCE_PASSWORD=CHANGE_ME_STRONG_PASSWORD"
   Environment="TEAMLINESS_WORKDAY_MODE=raas"
   Environment="TEAMLINESS_WORKDAY_RAAS_URL=https://wd5-services1.myworkday.com/ccx/service/customreport2/tenant/owner/report?format=json"
   ExecStart=/usr/bin/java -XX:+UseZGC -Xms256m -Xmx512m -jar /opt/teamliness/backend/teamliness-backend.jar
   Restart=always
   RestartSec=5

   [Install]
   WantedBy=multi-user.target
   ```
3. Enable and start the service (Flyway automatically validates and applies schema migrations on startup):
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable --now teamliness-backend
   ```

### 4.3 Build & Serve the Frontend SPA via Nginx
1. Build the static production bundle:
   ```bash
   cd frontend
   npm ci
   npm run build
   sudo mkdir -p /var/www/teamliness
   sudo cp -r dist/* /var/www/teamliness/
   ```
2. Configure `/etc/nginx/sites-available/teamliness.conf` to serve the Glassmorphic SPA and reverse-proxy `/api` and `/actuator` to Spring Boot:
   ```nginx
   server {
       listen 80;
       server_name teamliness.example.com;

       root /var/www/teamliness;
       index index.html;

       # Reverse-proxy REST API requests to Spring Boot
       location /api/ {
           proxy_pass http://127.0.0.1:8080/api/;
           proxy_http_version 1.1;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }

       location /actuator/health {
           proxy_pass http://127.0.0.1:8080/actuator/health;
       }

       # SPA client-side routing fallback
       location / {
           try_files $uri $uri/ /index.html;
       }
   }
   ```

---

## 5. Deploying as Containers in the Cloud (Google Cloud Platform — Cloud Run & Cloud SQL)

Teamliness is architected to run statelessly in containers on **Google Cloud Run**, backed by **Google Cloud SQL for PostgreSQL 16**, **Google Artifact Registry**, and **Google Secret Manager**.

```
┌─────────────────────────────────────────────────────────────────────────┐
│ Google Cloud Platform (GCP)                                             │
│                                                                         │
│  ┌───────────────────────────┐       ┌───────────────────────────────┐  │
│  │ Cloud Run (Frontend)      │       │ Cloud Run (Backend API)       │  │
│  │ teamliness-frontend       │──────▶│ teamliness-backend (Java 21)  │  │
│  │ (Nginx + Vite SPA Bundle) │ /api  │ Flyway + Actuator /health     │  │
│  └───────────────────────────┘       └───────────────┬───────────────┘  │
│                                                      │ Cloud SQL Proxy  │
│                                                      ▼                  │
│                                      ┌───────────────────────────────┐  │
│                                      │ Cloud SQL for PostgreSQL 16   │  │
│                                      │ (teamliness-pg16)             │  │
│                                      └───────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.1 Initialize GCP Project, APIs & Artifact Registry
```bash
export PROJECT_ID="your-gcp-project-id"
export REGION="europe-west1"

gcloud config set project "${PROJECT_ID}"

# 1. Enable required GCP services
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com \
  secretmanager.googleapis.com

# 2. Create Docker Artifact Registry repository
gcloud artifacts repositories create teamliness \
  --repository-format=docker \
  --location="${REGION}" \
  --description="Teamliness Backend and Frontend container images"
```

### 5.2 Provision Cloud SQL for PostgreSQL 16 & Secret Manager
```bash
# 1. Create Cloud SQL PostgreSQL 16 instance
gcloud sql instances create teamliness-pg16 \
  --database-version=POSTGRES_16 \
  --cpu=2 \
  --memory=4GiB \
  --region="${REGION}"

# 2. Create application database and user
gcloud sql databases create teamliness --instance=teamliness-pg16
gcloud sql users create teamliness \
  --instance=teamliness-pg16 \
  --password="YOUR_SECURE_DB_PASSWORD"

# 3. Store DB password in Google Secret Manager
printf "YOUR_SECURE_DB_PASSWORD" | gcloud secrets create teamliness-db-password --data-file=-
```

### 5.3 Build & Push Container Images to Artifact Registry
Using `backend/Dockerfile` (multi-stage Gradle build + Eclipse Temurin JRE 21) and `frontend/Dockerfile` (multi-stage Node build + Nginx):

```bash
export REGISTRY="${REGION}-docker.pkg.dev/${PROJECT_ID}/teamliness"

# Build & push Backend image
gcloud builds submit ./backend \
  --tag "${REGISTRY}/teamliness-backend:latest"

# Build & push Frontend image
gcloud builds submit ./frontend \
  --tag "${REGISTRY}/teamliness-frontend:latest"
```

### 5.4 Deploy Backend & Frontend Services to Google Cloud Run
```bash
export INSTANCE_CONNECTION_NAME="${PROJECT_ID}:${REGION}:teamliness-pg16"

# 1. Deploy teamliness-backend attached to Cloud SQL
gcloud run deploy teamliness-backend \
  --image="${REGISTRY}/teamliness-backend:latest" \
  --region="${REGION}" \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances="${INSTANCE_CONNECTION_NAME}" \
  --set-env-vars="SPRING_DATASOURCE_URL=jdbc:postgresql:///teamliness?cloudSqlInstance=${INSTANCE_CONNECTION_NAME}&socketFactory=com.google.cloud.sql.postgres.SocketFactory" \
  --set-env-vars="SPRING_DATASOURCE_USERNAME=teamliness" \
  --set-secrets="SPRING_DATASOURCE_PASSWORD=teamliness-db-password:latest"

# 2. Capture Backend Cloud Run URL and deploy teamliness-frontend
export BACKEND_URL=$(gcloud run services describe teamliness-backend --region="${REGION}" --format="value(status.url)")

gcloud run deploy teamliness-frontend \
  --image="${REGISTRY}/teamliness-frontend:latest" \
  --region="${REGION}" \
  --platform=managed \
  --allow-unauthenticated \
  --set-env-vars="BACKEND_API_URL=${BACKEND_URL}"
```

---

## 6. Continuous Integration & Deployment (GitHub Actions & Cloud Build)

* **Every Commit & Pull Request (`.github/workflows/ci.yml`):**
  * Automatically executes `./gradlew check jacocoTestReport --no-daemon` (Java 21 + Cucumber ATDD + JaCoCo coverage) and `npm ci && npm run lint && npm test -- --coverage && npm run build` (TypeScript + Vitest ATDD + Vite production bundle).
  * Publishes the generated **Backend & Frontend HTML Living Documentation Reports** (`backend-atdd-living-docs` and `frontend-atdd-living-docs`) as downloadable build artifacts on every run.
* **Automated GCP Push Pipeline (`cloudbuild.yaml` & `.github/workflows/deploy-gcp.yml`):**
  * Authenticates via keyless **GCP Workload Identity Federation**, runs the full single-run ATDD verification suite, builds and tags multi-stage container images, pushes to **Google Artifact Registry**, and deploys new revisions to **Google Cloud Run**.
