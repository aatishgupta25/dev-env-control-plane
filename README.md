# Dev Environment Control Plane

A small learning project for understanding control-plane patterns: desired state, reconciliation, idempotent infrastructure operations, and cleanup.

The API stores requested development environments in PostgreSQL. A background reconciler compares that desired state with Kubernetes and creates or removes an isolated namespace for each environment.

## Why this shape?

The project intentionally separates three concerns:

- **API and persistence** record what should exist.
- **Reconciliation** repeatedly moves actual state toward desired state.
- **Provisioning** owns Kubernetes-specific side effects behind a small interface.

That separation keeps the control-plane logic testable without needing a Kubernetes cluster. Provisioning uses apply/delete operations so repeated reconciliation is safe rather than treating every retry as a new request.

An environment moves through a deliberately small lifecycle:

`PENDING -> READY -> DELETING`

Provisioning failures move it to `ERROR` instead of blocking reconciliation of unrelated environments.

## MVP API

```text
POST   /environments
GET    /environments
GET    /environments/{id}
DELETE /environments/{id}
```

Example request:

```json
{
  "name": "compiler-project",
  "template": "ubuntu:24.04"
}
```

The `template` is simply the container image used for the workspace deployment. Keeping templates this small avoids building a separate template system before it is useful.

## Run locally

Requirements: Java 21, Maven, Docker, and access to a Kubernetes context such as Docker Desktop or minikube.

```bash
docker compose up -d
mvn spring-boot:run
```

Run the unit tests with:

```bash
mvn test
```

## Scope

This is intentionally not a production developer platform. Authentication, quotas, secrets, networking policy, multi-cluster scheduling, persistent volumes, and a UI are left out so the project stays focused on the control-plane mechanics themselves.
