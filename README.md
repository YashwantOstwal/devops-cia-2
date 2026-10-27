# Campus Equipment Booking — Set D

Java 17 / Spring Boot REST API, based on the supplied student starter project.
Inventory is in memory and resets whenever the application restarts.

## Assigned changes
- Corrected the capacity comparison in `BookingService.create`: requested quantity must be positive and at most the available quantity.
- Verified the starter's nonblank borrower rule; added focused HTTP validation handling and tests for valid, empty, whitespace-only and missing borrower IDs. Invalid requests return HTTP 400 and do not change inventory or create bookings.
- Kept all six original JUnit tests and added seven regression tests.

## Git workflow
Working branch: `feature/borrower-validation`.
```bash
git branch --show-current
git log --oneline --max-count=5
git status
```

## Build and tests
Requirements: JDK 17, Maven, Git. Run from project root:
```bash
mvn clean test
mvn clean package
```
Resulting JAR: `target/campus-equipment-booking-1.0.0.jar`.

## Run locally
```bash
java -jar target/campus-equipment-booking-1.0.0.jar
```
Health check: `http://localhost:8080/actuator/health` (expected `{"status":"UP"}`).
API: `/api/equipment`, `/api/equipment/{id}`, `/api/bookings`, `/api/bookings/{id}`, `/api/bookings/{id}/cancel`.

## Docker
```bash
docker build -t campus-equipment-booking:set-d .
docker run -d --name campus-booking -p 8080:8080 campus-equipment-booking:set-d
docker ps
curl http://localhost:8080/actuator/health
```
Stop with `docker rm -f campus-booking`.

## Minikube / Kubernetes
With Docker Desktop running:
```bash
minikube start --driver=docker
minikube image load campus-equipment-booking:set-d
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl rollout status deployment/campus-equipment-booking
kubectl get deployments,pods,services
kubectl port-forward service/campus-equipment-booking 18080:8080
```
In a second terminal: `curl http://localhost:18080/actuator/health`.
Then scale and verify:
```bash
kubectl scale deployment/campus-equipment-booking --replicas=2
kubectl get deployments,pods
```
Expected: deployment shows `2/2` READY and two Running pods.
Service is also defined as NodePort 30080.

## CI
`.github/workflows/ci.yml` runs Maven tests, packages the JAR and builds the Docker image on GitHub Actions. The Minikube verification is performed locally for the assessment. A remote push is needed to run the GitHub workflow.
