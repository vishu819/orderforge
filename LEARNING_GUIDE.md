# OrderForge: build and learn

## Main objective

Build a small, explainable order-management system to assess and improve Vishal's data modeling and system design skills while learning Docker, Kubernetes, Jenkins, and cloud deployment through implementation.

Success means being able to explain the design choices, demonstrate working behavior, and diagnose a failure. Completing a technology checklist alone is not the goal.

## Working agreement

- Vishal writes the main application code: models, business rules, endpoints, service communication, and relevant tests.
- The assistant acts as a teacher, guide, and reviewer. Ask for Vishal's design first, explain concepts as they become relevant, and provide small tasks with observable acceptance criteria.
- Start with questions and hints. Provide a skeleton or full implementation when Vishal asks for it.
- Review correctness, modeling, tradeoffs, and maintainability, and explain the reasons behind feedback. Leave application changes for Vishal to implement unless he asks for direct edits.
- Infrastructure can be scaffolded with explanations, but Vishal should understand and run each step.
- Work on one small milestone at a time. After each milestone, record what worked, what failed, and what was learned.
- Adapt tasks to demonstrated knowledge; avoid lengthy lessons on Java and Spring Boot basics.

## Build and learn loop

Keep questions, answers, and decisions in the [learning journal](docs/learning-journal.md). Ask a small number of questions that affect the current design, invite Vishal's reasoning, then record the reviewed explanation. The immediate priority is the basic deployment with manual verification; automated test work is deferred per Vishal's preference. Production enhancements follow the initial deployment.

1. **Design:** Vishal proposes a model or approach and states assumptions.
2. **Discuss:** inspect edge cases and alternatives; choose the smallest useful implementation.
3. **Build:** Vishal writes the code.
4. **Verify:** run realistic success and failure scenarios; add focused tests where useful.
5. **Review:** explain findings and have Vishal improve the implementation.
6. **Reflect:** capture a decision and a lesson before moving on.

## Weekend scope

Two services: Product owns the catalog; Order owns orders and requests product information over HTTP. Start with in-memory storage. Discuss a relational schema without requiring a database implementation during the weekend.

Keep authentication, payments, messaging, service discovery servers, and a frontend outside the initial scope. Treat additional features as later exercises. Cloud deployment depends on account setup and an agreed spending limit; local Kubernetes remains a demonstrable milestone.

| Milestone | Design and learning focus | Evidence of completion |
| --- | --- | --- |
| 0. Bootstrap | Independent services, ports, Maven modules | Both services run locally and health endpoints return UP; completed by Vishal |
| 1. Model the domain | Entities, identifiers, relationships, money, invariants, ownership | Written model proposal and reviewed example scenarios |
| 2. Product API | Contract, validation, lookup, error behavior | Known product returns 200; unknown product returns 404 |
| 3. Order API | Order items, price snapshots, totals, boundaries | Valid order succeeds; invalid inputs and missing products have defined behavior |
| 4. Service failures | Timeouts, dependency outages, retry risks | Product outage produces a bounded, understandable Order response |
| 5. Docker | Images, build context, container networking | Both services run through Compose and communicate |
| 6. Kubernetes | Deployments, Services, configuration, readiness/liveness | Both services run locally; observe recovery after deleting a pod |
| 7. Jenkins | Test, package, image build, deployment configuration | A pipeline run builds the services and images; deployment is demonstrated against the configured environment |
| 8. Cloud | Registry, deployment, access, logs, cost and cleanup | Services run in the selected cloud; deployment and cleanup steps are documented |

## Current progress and exercise

Both services have been implemented and run through Docker Compose and local Kubernetes. Saved manifests include one Deployment and one ClusterIP Service per application, with readiness probes. Vishal confirmed successful order creation and retrieval through Kubernetes. This records user-reported verification, not a fresh automated check.

See [Kubernetes notes](docs/kubernetes-notes.md) for setup, commands, observed behavior, and limitations.

**Next:** discuss startup versus readiness versus liveness, then add startup and liveness probes. Pending question: Product is unavailable, but Order can return existing orders; should Order's liveness fail, and why?

**Deferred:** the Docker broken-URL troubleshooting exercise, at Vishal's request. Jenkins, registry delivery, and cloud deployment remain planned. Automated tests remain deferred. The original weekend target is now a learning scope rather than a completed schedule.

## Review lens

- **Data modeling:** relationships, ownership, types, invariants, and historical correctness.
- **System design:** clear boundaries, contracts, failure behavior, and justified simplicity.
- **Implementation:** behavior matches the chosen design; errors are understandable; tests exercise meaningful scenarios.
- **Operations:** reproducible builds, configuration, health checks, logs, and deployment behavior.
- **Understanding:** Vishal can explain why each choice was made and what would change at greater scale.

## Session notes

After each milestone append a short entry:

```text
Milestone:
Implemented and verified:
Decision and reason:
Failure or surprising behavior:
What I learned:
Next small task:
```
