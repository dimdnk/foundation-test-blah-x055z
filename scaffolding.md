# Scaffolding a New Service from This Template

This document lists every token that must be replaced when creating a new
foundation service from `foundation-microservice-project-layout`.

---

## Token Map

The table below maps each template token to what you should substitute.
The example column uses a hypothetical **reporting** service.

| Token                             | Kind                                       | Example replacement                    |
| --------------------------------- | ------------------------------------------ | -------------------------------------- |
| `servicename`                     | lowercase slug                             | `reporting`                            |
| `Servicename`                     | PascalCase                                 | `Reporting`                            |
| `SERVICENAME`                     | UPPER_CASE (env var prefixes, queue names) | `REPORTING`                            |
| `foundation-servicename-service`  | kebab-case artifact/image/app name         | `foundation-reporting-service`         |
| `com.iqkv.foundation.servicename` | Java base package                          | `com.iqkv.foundation.reportingservice` |
| `svc_servicename_dba`             | Postgres DB user                           | `svc_reporting_dba`                    |
| `svc_servicename_rmq`             | RabbitMQ user                              | `svc_reporting_rmq`                    |
| `iqkv.servicename.*`              | RabbitMQ queue name prefix                 | `iqkv.reporting.*`                     |
| `Servicename Service API`         | OpenAPI title in `OpenApiConfig`           | `Reporting Service API`                |

---

## Step-by-Step

### 1. Copy and rename the repository

```
cp -r foundation-microservice-project-layout foundation-reporting-service
cd foundation-reporting-service
```

### 2. Global text replacement

Run these substitutions across the entire tree (IDE rename, `sed`, or your
preferred tool). Apply them **in this order** — longer/more-specific tokens first
to avoid partial matches.

```
com.iqkv.foundation.servicename  →  com.iqkv.foundation.reportingservice
foundation-servicename-service   →  foundation-reporting-service
iqkv.servicename.                →  iqkv.reporting.
svc_servicename_dba              →  svc_reporting_dba
svc_servicename_rmq              →  svc_reporting_rmq
ServicenameApplication           →  ReportingApplication
Servicename Service API          →  Reporting Service API
servicename                      →  reporting
Servicename                      →  Reporting
```

> **Tip — IntelliJ IDEA:** `Edit → Find → Replace in Files…`,
> enable _Match case_ and _Words_, work through each row above.
>
> **Tip — shell (Linux/macOS):**
>
> ```bash
> grep -rl 'servicename' . | xargs sed -i 's/servicename/reporting/g'
> ```
>
> Run the more-specific patterns before the generic `servicename` one.

### 3. Rename files and directories

Rename any file or directory whose name contains `servicename`:

| From                                   | To                                                        |
| -------------------------------------- | --------------------------------------------------------- |
| `src/.../servicename/`                 | `src/.../reportingservice/`                               |
| `ServicenameApplication.java`          | `ReportingApplication.java`                               |
| `docker/postgres/init-servicename.sql` | `docker/postgres/init-reporting.sql`                      |
| `docker/dbgate/connections.jsonl`      | update `server`, `user`, `database`, `displayName` fields |

### 4. Update `pom.xml`

| Field           | Value                          |
| --------------- | ------------------------------ |
| `<artifactId>`  | `foundation-reporting-service` |
| `<name>`        | `Foundation Reporting Service` |
| `<description>` | your service description       |

### 5. Update `compose.base.yaml` / `compose.yaml` / `compose.container.yaml`

All three files were already processed by the global replacement in step 2.
Double-check:

- Service keys: `postgres-reporting-service`, `rabbitmq-reporting-service`, `reporting-service`
- Network name: `foundation-reporting-network-dev`
- Volume names: `iqkv_reporting_postgres_data_dev`, etc.
- Subnet: pick the next free `/16` block — see the comment in `compose.base.yaml`
  under `networks.reporting-service-network.ipam`

### 6. Update `SecurityConfig`

In `SecurityConfig.java`, change the admin path matcher to the actual API prefix, and update the public path matcher to match:

```java
.requestMatchers("/api/v1/reporting/public/**").permitAll()
.requestMatchers("/api/v1/reporting/admin/**").hasAuthority("PLATFORM_ADMIN")
```

### 7. Update `TenantExtractionFilter`

In `TenantExtractionFilter.java`, update `shouldNotFilter` for both the public and admin prefixes:

```java
|| path.startsWith("/api/v1/reporting/public/")
|| path.startsWith("/api/v1/reporting/admin/");
```

### 8. Update `RabbitMQConfig`

Queue name constants use `iqkv.reporting.*` after the global replacement.
Review and add any domain-specific queues and routing keys the service needs.

### 9. Update `OpenApiConfig`

```java
.title("Reporting Service API")
.description("Reporting Service — ...")
```

### 10. Update `application.yml`

```yaml
spring:
    application:
        name: foundation-reporting-service

iqkv:
    db:
        name: ${DB_NAME:reportingservice}
        username: ${DB_USERNAME:svc_reporting_dba}
        password: ${DB_PASSWORD:svc_reporting_dba}
    rabbitmq:
        username: ${RABBITMQ_USERNAME:svc_reporting_rmq}
        password: ${RABBITMQ_PASSWORD:svc_reporting_rmq}
```

### 11. Update `docker/postgres/init-reporting.sql`

Change the schema name, database name, and DBA user to match the new service.

### 12. Update `docker/dbgate/connections.jsonl`

Change `server`, `user`, `password`, `database`, and `displayName` for both
Postgres and RabbitMQ connection entries.

### 13. Update `TenantExtractionFilter` admin skip Javadoc

Update the Javadoc in `TenantExtractionFilter` to name the actual admin path.

### 14. Replace the dev RSA public key

`src/main/resources/keys/public.pem` contains the shared **development** public key
from the IAM service — it lets the service start locally and accept tokens issued
by a local IAM instance without any extra setup.

For a real service you must replace it with your environment's actual public key,
or leave the file as-is and rely on the `JWT_PUBLIC_KEY_PATH` env var in deployed
profiles (sit/uat/prd) to point at a mounted secret instead.

The private key is **never** placed in a resource-server repo. Only the IAM service
holds `private.pem`.

### 15. Delete this file

```
rm scaffolding.md
```

---

## Checklist

- [ ] `pom.xml` artifact ID and name updated
- [ ] Java base package renamed (`com.iqkv.foundation.reportingservice`)
- [ ] Main application class renamed (`ReportingApplication`)
- [ ] `spring.application.name` in `application.yml` updated
- [ ] DB credentials (`svc_reporting_dba`) updated in `application.yml` and SQL init script
- [ ] RabbitMQ credentials (`svc_reporting_rmq`) updated in `application.yml`
- [ ] RabbitMQ queue names updated in `RabbitMQConfig` (`iqkv.reporting.*`)
- [ ] `SecurityConfig` admin path updated (`/api/v1/reporting/admin/**`)
- [ ] `TenantExtractionFilter` admin skip path updated
- [ ] `OpenApiConfig` title and description updated
- [ ] `compose.base.yaml` subnet assigned (next free `/16` after `172.25.0.0/16`)
- [ ] `docker/postgres/init-reporting.sql` schema, database, and user updated
- [ ] `docker/dbgate/connections.jsonl` server names and credentials updated
- [ ] `src/main/resources/keys/public.pem` replaced with the real environment key (or env var configured)
- [ ] `Scaffolding.md` deleted
