# Dev Environment Control Plane

A control plane for provisioning isolated Kubernetes development environments from reusable workspace templates.

The API stores requested development environments in PostgreSQL. A background reconciler continuously moves Kubernetes resources toward that desired state, creating and removing an isolated namespace for each environment.

## Architecture

The service separates three concerns:

- **API and persistence** record the desired environment state.
- **Reconciliation** moves actual cluster state toward the persisted desired state.
- **Provisioning** owns Kubernetes-specific side effects behind an interface.

This separation keeps control-plane behavior independently testable and isolates Kubernetes operations from the API layer. Provisioning uses idempotent apply/delete operations so reconciliation can safely retry operations.

An environment moves through the lifecycle:

`PENDING -> READY -> DELETING`

Provisioning failures move the environment to `ERROR` without blocking reconciliation of unrelated environments.

## Workspace templates

Environment requests select a reusable workspace template. The template registry provides Java 21, Python 3.12, and Ubuntu workspaces and resolves each template to its corresponding container image before the desired state is persisted.

Example request:

```json
{
  "name": "compiler-project",
  "template": "java"
}
```

## API

```text
POST   /environments
GET    /environments
GET    /environments/{id}
DELETE /environments/{id}
```

## Run locally

Requirements: Java 21, Maven, Docker, and access to a Kubernetes context such as Docker Desktop or minikube.

```bash
docker compose up -d
mvn spring-boot:run
```

Run the tests with:

```bash
mvn test
```
