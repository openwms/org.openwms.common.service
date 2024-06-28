FROM amazoncorretto:21-alpine as builder
WORKDIR application
ARG JAR_FILE=target/openwms-common-service-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM amazoncorretto:21-alpine
ARG JAVA_OPTS="-Xss512k"
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher
