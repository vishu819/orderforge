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

## Structure

Each service has empty `controller`, `service`, `model`, and `dto` packages for your application code. Only the Spring Boot entry points, configuration, and generated context tests are provided.

The original IntelliJ `src/Main.java` is preserved but is outside the Maven modules. It is not used by either service.

## First coding task

Before coding, complete the short domain modeling exercise in the learning guide and review the proposal together.

Implement `GET /products/{id}` in product-service. Define a product with id, name, and price. Return a seeded product from in-memory data and return HTTP 404 when the ID is unknown. Keep the lookup logic in the service package and the endpoint in the controller package.

Then implement `POST /orders` in order-service, calling product-service to retrieve the product and calculate the order total.

## Verify

In each service directory run `./mvnw test`.

## Next milestones

1. Product endpoint and order endpoint with an HTTP call between services.
2. Dockerfiles and Docker Compose.
3. Kubernetes Deployments and Services.
4. Jenkins build and deployment pipeline.
5. Cloud deployment and resource cleanup.

Infrastructure files will be added during their learning stages.

For Docker revision, open the [Docker learning guide](docs/docker-learning-guide.html) in a browser. It explains our configuration, troubleshooting, production gaps, and self-check questions.
