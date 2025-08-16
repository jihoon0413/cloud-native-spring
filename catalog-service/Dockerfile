FROM eclipse-temurin:17 AS builder
WORKDIR workspace

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} catalog-service.jar
RUN java -Djarmode=layertools -jar catalog-service.jar extract
# 계층 jar 를 적용해서 아카이브에서 계층 추출

FROM eclipse-temurin:17
WORKDIR workspace
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./
# 의존성, 스프링 부트 로더, 스냅숏, 애플리케이션 계층 계층화

ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]