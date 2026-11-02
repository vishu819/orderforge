# Kubernetes: OrderForge learning notes

Progress recorded on 2 November 2026 from saved manifests and Vishal's reported terminal/browser results. No fresh cluster commands were run for this documentation update.

## What is running

Docker Desktop provisions a single-node kind cluster. The kubectl context is `docker-desktop`, the kind cluster name is `desktop`, and the observed node is `desktop-control-plane` (ARM64, Ready; Kubernetes v1.36.1 at setup).

Both services have one replica and run in the namespace selected by kubectl; our exercises used the default namespace. The manifests do not specify a namespace.

| File | Purpose |
| --- | --- |
| ../k8s/product-deployment.yml | Maintains a Product Pod using orderforge-product-service:v1; readiness on 8081 |
| ../k8s/product-service.yml | ClusterIP address product-service:8081 selecting app=product-service |
| ../k8s/order-deployment.yml | Maintains an Order Pod using orderforge-order-service:v1; readiness on 8082; Product URL in an environment variable |
| ../k8s/order-service.yml | ClusterIP address order-service:8082 selecting app=order-service |

## Concepts in our setup

- Deployment declares the desired state and manages ReplicaSets; a ReplicaSet maintains the requested Pods.
- A replacement Pod has a new identity. Reconciliation does not restore JVM memory.
- Labels on the Pod template match the Deployment selector and the Service selector.
- A ClusterIP Service provides stable discovery and routes to eligible endpoints. Pods can change addresses.
- Order calls http://product-service:8081 in the same namespace. This resolves through Kubernetes, independently of Compose.
- Port-forward opens local access for development. It is not persistent external hosting and may need restarting after the selected Pod is replaced.
- The readiness probe is an HTTP request by Kubernetes; it does not use curl in the container.

Request path: laptop:28082 -> port-forward -> Order Pod -> product-service:8081 -> Product Pod.

## Image loading: what we learned

Initially the temporary Product Pod showed ErrImageNeverPull. The Docker image existed on the laptop but not in the node's runtime. After the kind CLI detected cluster desktop, kind load docker-image made the image available. No second cluster was created. Docker Desktop's system-container visibility setting was part of the discovery troubleshooting.

For a repeat deployment from the repository root:

```bash
kubectl config current-context
kubectl --context docker-desktop get nodes

docker build -t orderforge-product-service:v1 ./product-service
docker build -t orderforge-order-service:v1 ./order-service
kind load docker-image orderforge-product-service:v1 --name desktop
kind load docker-image orderforge-order-service:v1 --name desktop

kubectl --context docker-desktop apply -f k8s/
kubectl --context docker-desktop rollout status deployment/product-service
kubectl --context docker-desktop rollout status deployment/order-service
kubectl --context docker-desktop get deployments,pods,services
```

The cluster must already exist and the images must be loaded into its nodes. Never means Kubernetes will not pull a missing image. Use new tags for subsequent code revisions and update the Deployment; reusing v1 does not automatically trigger a rollout and can leave old images running. Registry-based distribution comes later.

Run each port-forward in a separate terminal:

```bash
kubectl --context docker-desktop port-forward service/product-service 28081:8081
kubectl --context docker-desktop port-forward service/order-service 28082:8082
```

If the local port is occupied, stop the earlier forward with Ctrl+C or choose another local port. This does not require changing the Service port.

## Manual behavior confirmed by Vishal

- Temporary Product Pod reached 1/1 Running after image loading.
- Product returned Keyboard through localhost:28081/products/prod-1.
- Readiness configuration triggered a rollout: the new Product Pod became Ready before the old Pod terminated.
- Old Pod briefly displayed Error while terminating. Its exit details were not captured; the cause is unknown. The subsequent output showed the new Pod Running with zero restarts.
- Order creation and retrieval worked through localhost:28082, exercising Order-to-Product communication.
- A deliberate deletion-and-replacement experiment was explained, but completion was not explicitly confirmed.

No automated tests were run for these learning steps. Not every failure/error-code scenario has been manually rechecked in Kubernetes.

## Probes: implemented versus next

Both Deployments currently use readiness at /actuator/health/readiness with a 5-second initial delay, 5-second period, 2-second timeout, and failure threshold 3.

| Probe | Purpose | Failure behavior | Current state |
| --- | --- | --- | --- |
| Readiness | Should this Pod receive normal Service traffic? | Marks it unready; does not restart the container | Implemented |
| Startup | Has initialization succeeded? | Repeated failure restarts the container; other probes wait until startup succeeds | Planned |
| Liveness | Does this application need a restart? | Repeated failure restarts the container | Planned |

Spring readiness reflects its configured health group, not every possible order operation. Order's current readiness does not test Product availability. A Product outage should not automatically make Order's liveness fail: Order can still serve saved orders, and restarting it will not repair Product.

**Question for Vishal:** Explain that liveness decision in your own words. Answer pending.

## Limits and next steps

Orders remain in memory, disappear on JVM restart, and are not shared across replicas. Keep one Order replica for this exercise; a volume alone cannot persist a ConcurrentHashMap. Kubernetes Services and automatic Pod replacement do not solve persistence or idempotency.

Startup/liveness probes, resource requests/limits, explicit Kubernetes security settings, ConfigMap/Secret management, ingress, registry delivery, Jenkins, cloud deployment, and production validation remain future work. Current v1 local-image manifests are a learning configuration, not production configuration.
