# Jmix Observability Tracing Sample

This project is part of the **Jmix Observability Guide Series** and demonstrates distributed tracing and metrics monitoring across multiple Jmix applications.

The goal of the guide series is to showcase how Jmix applications can be monitored, traced, and analyzed using modern, open-source observability tooling. This includes OpenTelemetry as the telemetry transport protocol and the Grafana OpenTelemetry LGTM stack as the local observability backend.

In this example, we use two Jmix applications – the Petclinic backend and the Petclinic Portal frontend – which interact with each other and generate distributed traces. The trace data is centrally collected using OpenTelemetry and visualized in Grafana for end-to-end request tracking across services.

The project is used in two guides:

- [Observability: Distributed Tracing](https://docs.jmix.io/jmix/observability-tracing-guide): traces across the Portal and the backend.
- [Observability: Monitoring](https://docs.jmix.io/jmix/observability-monitoring-guide): Jmix metrics collected by Prometheus and shown on the Jmix 3 Monitoring dashboard in Grafana.

---

## Build the JAR Files

Before starting the applications using Docker Compose, you need to build the JAR files.

### Build petclinic (backend)
```bash
./gradlew -Pvaadin.productionMode=true --include-build jmix-petclinic-2 :jmix-petclinic-2:clean :jmix-petclinic-2:bootJar -x test --no-build-cache
```


### Build petclinic-portal (frontend)
```bash
./gradlew -Pvaadin.productionMode=true --include-build jmix-petclinic-portal :jmix-petclinic-portal:clean :jmix-petclinic-portal:bootJar -x test --no-build-cache
```

Once the JARs are built, Docker Compose can copy them into the application containers using the provided Dockerfiles.

## Start the Infrastructure

Start PostgreSQL, the Grafana OpenTelemetry LGTM stack and Prometheus:

```bash
docker compose -f docker/docker-compose.yaml up -d
```

The LGTM container includes an OpenTelemetry Collector, Loki, Grafana, Tempo, Prometheus, and Pyroscope. It accepts OTLP data on `localhost:4317` and `localhost:4318`. Open Grafana at http://localhost:3000 and log in with username `admin` and password `admin`.

The separate Prometheus service scrapes the `/actuator/prometheus` endpoint of both applications and is available at http://localhost:9090. Grafana gets it as the `Prometheus Actuator` data source.

Run the Petclinic backend and Portal from the IDE or their Gradle `bootRun` tasks. Their local configuration exports logs and traces to the LGTM container.

Alternatively, after building both JAR files, start the applications in Docker with the `app` profile:

```bash
docker compose -f docker/docker-compose.yaml --profile app up --build -d
```

To stop the infrastructure, use:

```bash
docker compose -f docker/docker-compose.yaml down
```
