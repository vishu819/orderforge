FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -B dependency:go-offline

COPY lombok.config ./
COPY src/ src/
RUN ./mvnw -B package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre

RUN groupadd --gid 10001 appgroup \
    && useradd --uid 10001 --gid appgroup \
       --no-create-home appuser

WORKDIR /app

COPY --from=build \
    /build/target/order-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 8082

USER 10001:10001

ENTRYPOINT ["java", "-jar", "app.jar"]
