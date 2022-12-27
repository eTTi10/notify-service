FROM openjdk:11-jdk-slim as builder
WORKDIR application
COPY subprojects/boot/build/libs/application.jar ./
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:11-jre-slim
EXPOSE 8080
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]