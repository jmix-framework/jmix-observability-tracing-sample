# Distributed Tracing with Micrometer and OpenTelemetry - Guide Plan

:sample-project: jmix-observability-tracing-sample

**Note: This guide will be written in AsciiDoc format (.adoc), not Markdown**

## Opening Statement

In this guide, you will learn how to implement distributed tracing for your Jmix applications using Micrometer and OpenTelemetry to track requests across multiple services and gain complete visibility into your application's behavior.

## Structure Template

```asciidoc
= Observability: Distributed Tracing

In this guide, you will learn how to implement distributed tracing for your Jmix applications using Micrometer and OpenTelemetry to track requests across multiple services and gain complete visibility into your application's behavior.

[[requirements]]
== Requirements

Prerequisites and setup instructions...

[[what-we-build]]
== What We are Going to Build

Overview of the final result...

[[introduction-distributed-tracing]]
== Introduction to Distributed Tracing

Brief conceptual overview...

[[setting-up-tracing]]
== Setting Up Distributed Tracing

Infrastructure and configuration...

[[automatic-instrumentation]]
== Example 1: Automatic Instrumentation - Viewing Pet List

Simple example with zero code changes...

[[custom-tracing]]
== Example 2: Custom Tracing - Owner Registration

Complex business operation with custom spans...

[[best-practices]]
== Best Practices

Guidelines for effective tracing...

[[troubleshooting]]
== Troubleshooting

Common issues and solutions...

[[summary]]
== Summary

Recap of what was achieved...
```

## Content Plan

### [[requirements]]
== Requirements

- Link to development environment setup
- Reference to completed xref:observability-logging-guide:index.adoc[Centralized Logging Guide]
- Clone sample project with specific branch:

```asciidoc
* Clone the https://github.com/jmix-framework/{sample-project}[sample project^] and switch to `tracing-guide` branch:
+
[source,bash]
----
git clone https://github.com/jmix-framework/{sample-project}
cd {sample-project}
git checkout tracing-guide
----
```

### [[what-we-build]]
== What We are Going to Build

Brief description of enhancing the existing Petclinic Backend and Portal Frontend from the logging guide with distributed tracing capabilities.

The final application includes:

* *Automatic Instrumentation*: Zero-code tracing for HTTP requests, REST DataStore calls, and database queries
* *Custom Business Tracing*: Manual spans for complex operations like owner registration with business context
* *Grafana Tempo Integration*: Complete trace visualization and analysis
* *Cross-Service Correlation*: Track requests from Portal frontend through Backend to database

### [[introduction-distributed-tracing]]
== Introduction to Distributed Tracing

**Concept Through Example approach:**
- Don't just explain what distributed tracing is
- Show how a pet owner viewing their pets creates a trace across Portal → Backend → Database
- Demonstrate the problem: logs show isolated events, traces show the complete journey
- Use analogy: "Traces are like following a package delivery from warehouse to your door"

**Progressive complexity:**
1. Start with automatic instrumentation (no code changes)
2. Build up to custom business tracing
3. Show real-world debugging scenarios

### [[setting-up-tracing]]
== Setting Up Distributed Tracing

#### [[adding-tempo]]
=== Adding Grafana Tempo

Extend the existing docker-compose.yml from the logging guide:

.docker-compose.yml - Tempo service
[source,yaml,indent=0]
----
include::example$/infrastructure/docker-compose.yml[tags=tempo-service]
----

#### [[collector-configuration]]
=== Updating OpenTelemetry Collector

.otel-collector-config.yml - Trace export configuration
[source,yaml,indent=0]
----
include::example$/infrastructure/otel-collector-config.yml[tags=trace-exporters]
----

#### [[enabling-tracing-backend]]
=== Enabling Tracing in Petclinic Backend

Dependencies:
.build.gradle
[source,groovy,indent=0]
----
include::example$/petclinic/build.gradle[tags=tracing-dependencies]
----

Configuration:
.application.properties
[source,properties,indent=0]
----
include::example$/petclinic/src/main/resources/application.properties[tags=tracing-config]
----

#### [[enabling-tracing-portal]]
=== Enabling Tracing in Petclinic Portal

.build.gradle
[source,groovy,indent=0]
----
include::example$/portal/build.gradle[tags=tracing-dependencies]
----

.application.properties
[source,properties,indent=0]
----
include::example$/portal/src/main/resources/application.properties[tags=tracing-config]
----

#### [[verifying-setup]]
=== Verifying the Setup

1. Start the complete infrastructure:
+
[source,bash]
----
docker-compose up -d
----

2. Start both applications
3. Navigate to Grafana at http://localhost:3000
4. Verify Tempo data source is configured
5. Expected outcome: Tempo appears in data sources

### [[automatic-instrumentation]]
== Example 1: Automatic Instrumentation - Viewing Pet List

#### [[scenario-pet-list]]
=== The Scenario

Simple read operation demonstrating automatic tracing without any code changes:
- Pet owner logs into Portal at http://localhost:8081
- Portal loads list of their pets
- REST DataStore makes call to Backend
- Backend queries database
- Complete trace generated automatically

#### [[automatic-spans]]
=== What Gets Traced Automatically

Spring Boot's auto-instrumentation provides spans for:
- HTTP requests between Portal and Backend
- REST DataStore operations
- JPA query execution
- Database connection handling

#### [[viewing-trace-grafana]]
=== Viewing the Trace in Grafana

Steps to find and analyze the trace:
1. Open Grafana Explore
2. Select Tempo data source
3. Search for service "petclinic-portal"
4. Find traces with operation "GET /pets"
5. Screenshot showing complete trace timeline

#### [[understanding-spans]]
=== Understanding the Auto-Generated Spans

Analysis of what each span tells us:
- Portal request initiation span
- HTTP client span for REST call
- Backend HTTP server span
- JPA query span
- Database connection span
- Total request time breakdown

#### [[verification-automatic]]
=== Verification

Expected results when viewing pet list:
- Trace appears in Tempo within 30 seconds
- Minimum 4 spans: Portal UI → Portal HTTP client → Backend HTTP server → Database
- Trace ID correlation between services
- Response time under 500ms for healthy system

### [[custom-tracing]]
== Example 2: Custom Tracing - Owner Registration

#### [[scenario-owner-registration]]
=== The Business Scenario

Complex operation requiring custom instrumentation:
- New pet owner registers through Portal
- Multiple validation and creation steps
- Business context needed beyond automatic instrumentation

#### [[registration-service]]
=== Enhanced Registration Service with Custom Spans

.OwnerRegistrationService.java
[source,java,indent=0]
----
include::example$/petclinic/src/main/java/io/jmix/petclinic/service/OwnerRegistrationService.java[tags=start-class;register-owner-method;end-class]
----

Key elements:
- `@Observed` annotation for automatic span creation
- Manual `Observation` creation for specific business steps
- Business attributes added to spans

#### [[registration-validation]]
=== Adding Business Context to Validation Steps

.OwnerRegistrationService.java - Email validation
[source,java,indent=0]
----
include::example$/petclinic/src/main/java/io/jmix/petclinic/service/OwnerRegistrationService.java[tags=email-validation]
----

.OwnerRegistrationService.java - Phone validation
[source,java,indent=0]
----
include::example$/petclinic/src/main/java/io/jmix/petclinic/service/OwnerRegistrationService.java[tags=phone-validation]
----

#### [[registration-trace-analysis]]
=== Analyzing the Registration Trace

Complete trace breakdown:
1. Portal: Registration form submission
2. Backend: Email uniqueness check (custom span)
3. Backend: Phone validation (custom span)
4. Backend: Owner entity creation (custom span)
5. Backend: Database transaction commit

#### [[business-attributes]]
=== Adding Business Attributes to Spans

Example of enriching spans with business context:

.OwnerRegistrationService.java - Business attributes
[source,java,indent=0]
----
include::example$/petclinic/src/main/java/io/jmix/petclinic/service/OwnerRegistrationService.java[tags=business-attributes]
----

Low cardinality attributes (good for grouping):
- registration.type
- owner.category
- validation.step

High cardinality attributes (specific to instance):
- owner.id
- email.hash

#### [[correlating-logs-traces]]
=== Correlating Traces with Logs

Demonstrate how to:
- Find trace ID from failed registration
- Search logs by trace ID in Grafana Loki
- Jump from span to related log entries
- Complete visibility into registration failures

#### [[verification-custom]]
=== Verification

Test the custom tracing:
1. Register a new owner through Portal
2. Find registration trace in Tempo
3. Verify custom spans appear with business attributes
4. Test validation failure scenario
5. Expected: 6-8 spans total with business context

### [[best-practices]]
== Best Practices

#### [[sampling-strategy]]
=== Sampling Strategy

Different environments require different sampling:

.application.properties - Development sampling
[source,properties,indent=0]
----
include::example$/petclinic/src/main/resources/application-dev.properties[tags=sampling-config]
----

.application.properties - Production sampling
[source,properties,indent=0]
----
include::example$/petclinic/src/main/resources/application-prod.properties[tags=sampling-config]
----

#### [[custom-span-guidelines]]
=== Custom Span Guidelines

When to create custom spans:
- Business operations longer than 100ms
- Critical business workflows
- Error-prone operations
- Multi-step processes

Naming conventions:
- Use lowercase with dots: `owner.registration`
- Include business context: `owner.validation.email`
- Avoid high cardinality in span names

#### [[performance-considerations]]
=== Performance Considerations

Tracing overhead guidelines:
- Automatic instrumentation: <1% performance impact
- Custom spans: Minimal overhead with proper sampling
- Attribute guidelines: Limit to 10 attributes per span
- Avoid tracing in tight loops

#### [[security-considerations]]
=== Security Considerations

Protecting sensitive data in traces:
- Never include passwords or PII in span names
- Hash email addresses and phone numbers
- Use low cardinality alternatives for user identification
- Configure attribute filtering in production

### [[troubleshooting]]
== Troubleshooting

#### [[traces-not-appearing]]
=== Traces Not Appearing in Tempo

Common causes and solutions:
1. Check OpenTelemetry Collector logs
2. Verify endpoint configuration
3. Check sampling configuration
4. Validate Tempo data source in Grafana

.Terminal - Checking collector logs
[source,bash]
----
docker-compose logs otel-collector
----

#### [[broken-traces]]
=== Broken Traces Between Services

Solutions for incomplete traces:
1. Verify trace propagation headers
2. Check service naming consistency
3. Validate network connectivity
4. Review correlation ID passing

#### [[missing-custom-spans]]
=== Missing Custom Spans

Debugging custom instrumentation:
1. Verify @Observed annotation processing
2. Check Observation API usage
3. Validate ObservationRegistry configuration
4. Review span creation patterns

#### [[performance-issues]]
=== Performance Issues

If tracing impacts performance:
1. Reduce sampling rate
2. Limit custom span creation
3. Reduce attribute count
4. Consider async span export

### [[summary]]
== Summary

In this guide, we enhanced the Petclinic Backend and Portal applications from the centralized logging guide with comprehensive distributed tracing capabilities. We demonstrated two complementary approaches: automatic instrumentation that provides immediate value without code changes, and custom business tracing that adds meaningful context for complex operations.

The combination of automatic and custom tracing, along with correlation to centralized logs, provides complete observability into distributed request flows. You can now track requests from the Portal frontend through the Backend to the database, understanding exactly where time is spent and where errors occur.

The techniques shown here can be applied to any Jmix application architecture, from simple monoliths to complex microservice environments. With distributed tracing in place alongside centralized logging, you have the foundation for comprehensive application observability.

## Implementation Notes

### Code Examples Strategy
- All code examples will use `include::example$/...` directives
- Example project will have tagged sections for easy reference
- Complete working configuration files provided
- Docker Compose stack extends the logging guide setup

### Screenshot Requirements
- Grafana Tempo trace view showing complete request flow
- Custom span details with business attributes
- Correlation between traces and logs
- Performance comparison before/after tracing

### Cross-References
- Extensive references to the centralized logging guide
- Links to relevant Jmix and Spring Boot documentation
- References to OpenTelemetry best practices

### Verification Strategy
Each major section includes:
- Step-by-step verification instructions
- Expected outcomes and timings
- Common issues and quick fixes
- Screenshots of successful results
