# Dev Environment Control Plane

A small learning project for understanding control-plane patterns: desired state, reconciliation, idempotent infrastructure operations, and cleanup.

The MVP will expose a REST API that stores requested development environments in PostgreSQL and reconciles them into isolated Kubernetes namespaces.

## Scope

This project is intentionally small. It is not a production platform and avoids features such as authentication, quotas, multi-cluster scheduling, and a UI.
