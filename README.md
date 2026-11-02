# OrderForge

A weekend learning project: Java and Spring Boot microservices, followed by Docker, Kubernetes, Jenkins, and cloud deployment.

The main goals are data modeling, system design, and learning through building. Vishal writes the application code; the assistant guides and reviews. Start with the [learning guide](LEARNING_GUIDE.md) and write your proposal in [design notes](docs/design-notes.md).

## Requirements

- JDK 21 (select this as the IntelliJ Project SDK and Maven runner JRE)
- Maven Wrapper is included in each service; no separate Maven installation is required.
- Internet access for the first Maven run to download Maven and dependencies.

## Open in IntelliJ

Open the root `pom.xml` as a project and load both Maven modules. Set the project SDK, Maven importer JDK, and Maven runner JRE to JDK 21. Run `ProductServiceApplication` and `OrderServiceApplication` separately.

## Run from terminals

From the repository root, in terminal one:

```bash
cd product-service
./mvnw spring-boot:run
```

In terminal two, starting from the repository root:

```bash
cd order-service
./mvnw spring-boot:run
```

The terminal must also use JDK 21. On macOS, after installing JDK 21:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

Health checks:

- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health

Each should return a status of `UP` once its service starts.

## Current application

- Product Service: `GET /products/{id}` returns catalog data.
- Order Service: `POST /orders` captures product prices and calculates totals; `GET /orders/{orderId}` retrieves saved snapshots.
- Orders are stored only in JVM memory. Restarting or replacing Order loses them; replicas do not share data.
- Both services have multi-stage Dockerfiles and run as non-root users. Compose supplies networking and Product startup readiness checks.
- Local Kubernetes manifests provide Deployments, ClusterIP Services, and readiness probes. Vishal confirmed order creation and retrieval through Kubernetes.

The original IntelliJ `src/Main.java` is outside the Maven modules and is not used by either service.

## Docker Compose

From this repository root, with Docker Desktop running:

```bash
docker compose up -d --build
docker compose ps
```

Product is published on localhost:18081; Order on localhost:18082. Docker builds Java inside the build stages. Current image builds skip tests as part of the agreed basic learning workflow.

```bash
docker compose logs -f order-service
docker compose down
```

Stopping/removing the Order container loses its in-memory orders.

## Local Kubernetes

Read [Kubernetes setup and learning notes](docs/kubernetes-notes.md) before applying manifests. This setup uses Docker Desktop's kind cluster, locally loaded ARM64 images, and `imagePullPolicy: Never`; it is not a cloud deployment configuration.

Product is reached through port-forward 28081, and Order through 28082. These port-forwards must remain running.

## Learning and revision

- [Learning guide and current exercise](LEARNING_GUIDE.md)
- [Design notes](docs/design-notes.md)
- [Questions, answers, and learning journal](docs/learning-journal.md)
- [Docker learning guide](docs/docker-learning-guide.html) — open in a browser
- [Kubernetes notes](docs/kubernetes-notes.md)
- [Error response contract](docs/error-handling.md)

## Next milestones

1. Startup and liveness probes, resource settings, and further Kubernetes learning.
2. Revisit the deferred troubleshooting exercise.
3. Registry delivery and Jenkins pipeline.
4. Cloud deployment and resource cleanup.
5. Progressively define and implement production requirements.

Automated tests are currently deferred by agreement; manual verification is the active learning workflow. This project is not production ready.
