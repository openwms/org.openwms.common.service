# Purpose
The OpenWMS.org Common Service provides essential functionality to deal with `Locations`, `LocationGroups` and `TransportUnits`. For a
simple example, it offers an API to move a `TransportUnit` from a `Location` A to a `Location` B. Besides that, also other secondary
resources exist, like `TransportUnitTypes` or rule sets to define what type of `TransportUnit` can be put on what type of `Location`.

# Resources

| Module      | Build Status             | Quality | License | Maven Central  | Docker Hub | Chat |
|-------------|--------------------------|---------|---------|----------------|------------|------|
| Image       | [![Build status](https://github.com/openwms/org.openwms.common.service/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.common.service/actions/workflows/master-build.yml) | -- | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE) | -- | [![Docker pulls](https://img.shields.io/docker/pulls/openwms/org.openwms.common.service)](https://hub.docker.com/r/openwms/org.openwms.common.service) | [![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |
| [Library](https://github.com/openwms/org.openwms.common.service.lib) | [![Build status](https://github.com/openwms/org.openwms.common.service.lib/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.common.service.lib/actions/workflows/master-build.yml) | [![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.common.service.lib&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.common.service.lib) | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/openwms/org.openwms.common.service.lib/blob/master/LICENSE) | [![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.common.service.lib)](https://search.maven.org/search?q=a:org.openwms.common.service.lib) | -- | [![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |
| [UI Extension](https://github.com/interface21-io/org.openwms.common.service.ui) | [![Build status](https://github.com/interface21-io/org.openwms.common.service.ui/actions/workflows/master-build.yml/badge.svg)](https://github.com/interface21-io/org.openwms.common.service.ui/actions/workflows/master-build.yml) | [![Quality](https://sonar.openwms.cloud/api/project_badges/measure?project=org.openwms:org.openwms.common.service.ui&metric=alert_status)](https://sonar.openwms.cloud/dashboard?id=org.openwms:org.openwms.common.service.ui) | [![License](https://img.shields.io/badge/license-GPLv3-blue)](https://github.com/interface21-io/org.openwms.common.service.ui/blob/master/LICENSE) | -- | -- | [![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) |


**Find further Documentation on [Microservice Website](https://openwms.github.io/org.openwms.common.service)**

# Build
The module depends on OpenWMS.org CORE dependencies and one additional optional COMMON dependency that is only used in the enterprise
version and offers an additional API for the UI.

![MavenDependencies][1]

Build a runnable fat jar with the execution of all unit and in-memory database integration tests, but without a required [RabbitMQ](https://www.rabbitmq.com)
server to run: 

```
$ ./mvnw package
```

To also build and run with [RabbitMQ](https://www.rabbitmq.com) support call:

```
$ ./mvnw package -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST
```

This requires a [RabbitMQ](https://www.rabbitmq.com) server running locally with default settings.

# Run
## Run On Command Line
After the binary has been built it can be started from command line. By default, no other infrastructure services are required to run this
service.

Run in standalone mode:
```
$ java -jar target/openwms-common-service-exec.jar
```

To load some sample data, add another profile `DEMO`:
```
$ java -jar target/openwms-common-service-exec.jar --spring.profiles.active=DEMO
```

In a distributed environment the service configuration is fetched from the central [OpenWMS.org Configuration Service](https://github.com/spring-labs/org.openwms.configuration).
This behavior can be enabled by activating the Spring Profile `DISTRIBUTED`. Additionally, it makes sense to enable asynchronous
communication that requires a running [RabbitMQ](https://www.rabbitmq.com) instance - just add another profile `ASYNCHRONOUS`. If the latter
is not applied all asynchronous AMQP endpoints are disabled and the service does not send any events nor does it receive application events
from remote services. The AMQP protocol with the [RabbitMQ](https://www.rabbitmq.com) is currently the only supported message broker. But
switching to others, like [HiveMQ (MQTT)](https://www.hivemq.com) or [Apacha Kafka](https://kafka.apache.org/) is not rocket science.

```
$ java -jar target/openwms-common-service-exec.jar --spring.profiles.active=DISTRIBUTED,ASYNCHRONOUS
```
This requires a [RabbitMQ](https://www.rabbitmq.com) server running locally with default settings.

With these profiles applied the OpenWMS.org Configuration Service is tried to be discovered at service startup. The service fails to start
if no instance of the configuration service is available after a configured amount of retries.

## Run as Docker Container
Instead of building the software from the sources and run it as Java program on the JVM it can also be fetched as a Docker image from 
[Docker Hub](https://hub.docker.com/repository/docker/openwms/org.openwms.common.service) and started as a Docker container.

```
$ docker run openwms/org.openwms.common.service:latest
```

[1]: src/site/resources/images/maven-deps.drawio.png
