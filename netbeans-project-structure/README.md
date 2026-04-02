# NetBeans Project Structure - Lab 5

This Lab 5 implementation contains 6 microservices for the rental platform.
Each microservice has:

- Its own Maven WAR app
- Its own MySQL schema
- Its own app Docker image
- Its own DB Docker image

Total image target: **12 images**.

## Services

- auth-service
- catalog-service
- inventory-service
- reservation-service
- checkout-penalty-service
- frontend-service

## Prerequisites (Local + Cloud)

Install local tools:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk maven tomcat10 mysql-server mysql-client docker-ce docker-ce-cli containerd.io kubectl google-cloud-sdk
sudo systemctl enable --now mysql tomcat10 docker
```

Initialize Google Cloud CLI and target project:

```bash
gcloud init
gcloud auth login
gcloud config set project YOUR_PROJECT_ID
```

Create cluster and credentials:

```bash
gcloud container clusters create coe692lab5-cluster --num-nodes=3 --zone=northamerica-northeast1-a
gcloud container clusters get-credentials coe692lab5-cluster --zone=northamerica-northeast1-a
```

Install KubeMQ in cluster (replace token):

```bash
kubectl apply -f https://deploy.kubemq.io/init
kubectl apply -f https://deploy.kubemq.io/key/<kubemq-registration-token>
```

## NetBeans Import and Run

1. Open NetBeans.
2. Use **File -> Open Project**.
3. Open each service folder as Maven Web project.
4. Configure Tomcat 10 in NetBeans Services.

## Local Database Setup

Create app DB user:

```bash
sudo mysql -e "CREATE USER IF NOT EXISTS 'lab5_user'@'localhost' IDENTIFIED BY 'lab5_pass';"
sudo mysql -e "GRANT ALL PRIVILEGES ON *.* TO 'lab5_user'@'localhost'; FLUSH PRIVILEGES;"
```

Initialize all six schemas:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692/netbeans-project-structure"
mysql -u lab5_user -plab5_pass < auth-service/db/init.sql
mysql -u lab5_user -plab5_pass < catalog-service/db/init.sql
mysql -u lab5_user -plab5_pass < inventory-service/db/init.sql
mysql -u lab5_user -plab5_pass < reservation-service/db/init.sql
mysql -u lab5_user -plab5_pass < checkout-penalty-service/db/init.sql
mysql -u lab5_user -plab5_pass < frontend-service/db/init.sql
```

## Preflight Tomcat 8080 Check

Before deployment, verify port `8080` is free:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692"
./tests/preflight-8080.sh
```

If occupied, stop that process or adjust Tomcat connector.

## Build and Deploy to Tomcat

Build all services:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692/netbeans-project-structure"
mvn -f auth-service/pom.xml clean package
mvn -f catalog-service/pom.xml clean package
mvn -f inventory-service/pom.xml clean package
mvn -f reservation-service/pom.xml clean package
mvn -f checkout-penalty-service/pom.xml clean package
mvn -f frontend-service/pom.xml clean package
```

Deploy WARs:

```bash
sudo cp auth-service/target/auth-service.war /var/lib/tomcat10/webapps/
sudo cp catalog-service/target/catalog-service.war /var/lib/tomcat10/webapps/
sudo cp inventory-service/target/inventory-service.war /var/lib/tomcat10/webapps/
sudo cp reservation-service/target/reservation-service.war /var/lib/tomcat10/webapps/
sudo cp checkout-penalty-service/target/checkout-penalty-service.war /var/lib/tomcat10/webapps/
sudo cp frontend-service/target/frontend-service.war /var/lib/tomcat10/webapps/
sudo systemctl restart tomcat10
```

## Environment-Driven Runtime Config

Backend DB connections resolve values in this order:

1. Service-specific env vars (example `AUTH_DB_HOST`)
2. Shared env vars (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)
3. `src/main/resources/database.properties`

KubeMQ env vars used by async messaging:

- `KUBEMQ_ADDRESS` (fallback supports `kubeMQAddress`)
- `RESERVATION_EVENT_CHANNEL` (default `reservation-events`)

## Security Policy (JWT + Session Marker)

- Private/incognito browser sessions are blocked in frontend guard logic.
- All backend API endpoints now require:
  - a valid JWT (`Authorization: Bearer <token>` or `lab5_token` cookie), and
  - a valid session marker header: `X-LAB5-SESSION-MARKER`.
- `/api/health` endpoints are protected and return `401` without authentication.
- If JWT is present but marker is missing/invalid, services return `403`.
- Only `auth-service` login endpoint remains public:
  - `POST /auth-service/api/auth/login`

## Docker (12 Images)

From Lab 5 root:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692/docker"
DOCKER_HUB_USER=yourhubusername TAG_PREFIX=coe692-lab5 ./build-images.sh
DOCKER_HUB_USER=yourhubusername TAG_PREFIX=coe692-lab5 ./push-images.sh
```

Expected tags:

- `yourhubusername/coe692-lab5:auth-service-app`
- `yourhubusername/coe692-lab5:auth-service-db`
- `yourhubusername/coe692-lab5:catalog-service-app`
- `yourhubusername/coe692-lab5:catalog-service-db`
- `yourhubusername/coe692-lab5:inventory-service-app`
- `yourhubusername/coe692-lab5:inventory-service-db`
- `yourhubusername/coe692-lab5:reservation-service-app`
- `yourhubusername/coe692-lab5:reservation-service-db`
- `yourhubusername/coe692-lab5:checkout-penalty-service-app`
- `yourhubusername/coe692-lab5:checkout-penalty-service-db`
- `yourhubusername/coe692-lab5:frontend-service-app`
- `yourhubusername/coe692-lab5:frontend-service-db`

## Kubernetes Deployment (GCP)

Update image repository names in:

- `lab5_coe692/kube/lab5deployment.yaml`

Then apply:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692/kube"
kubectl apply -f lab5deployment.yaml
kubectl apply -f lab5service.yaml
kubectl get pods
kubectl get services
```

Frontend is exposed via `LoadBalancer` service `frontend-service-app`.

## Validation and Debug Tests

Unit tests:

```bash
mvn -f auth-service/pom.xml test
mvn -f reservation-service/pom.xml test
mvn -f inventory-service/pom.xml test
```

Smoke tests:

```bash
cd "/home/jashanpratap/Projects/coe692/lab5_coe692"
./tests/local-smoke.sh
./tests/security-smoke.sh
./tests/container-smoke.sh
./tests/k8s-smoke.sh
```

## Health Endpoints

```text
http://localhost:8080/auth-service/api/health
http://localhost:8080/catalog-service/api/health
http://localhost:8080/inventory-service/api/health
http://localhost:8080/reservation-service/api/health
http://localhost:8080/checkout-penalty-service/api/health
http://localhost:8080/frontend-service/
```

Expected behavior:
- Unauthenticated backend health calls -> `401`.
- Authenticated health calls with JWT + `X-LAB5-SESSION-MARKER` -> `200`.
- Frontend root redirects to login (`302`) when not authenticated.
