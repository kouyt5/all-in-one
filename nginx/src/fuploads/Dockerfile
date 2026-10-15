FROM gradle:8.14-jdk21-alpine@sha256:910abe9fbceb1a22779bee9ff4beeee118e92d8b9cae0105c5ecb44045c38175 AS builder
WORKDIR /app
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.workers.max=1 -Xmx512m"
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle gradle test bootJar --no-daemon
FROM eclipse-temurin:21-jre-alpine@sha256:51ab5e3302e7141ce665ca3ea85e8b5cd648eafbc3c0c90dd79d6537684e4555
RUN apk add --no-cache curl && addgroup -g 10001 app && adduser -D -u 10001 -G app app && mkdir -p /data/uploads && chown app:app /data/uploads
WORKDIR /app
COPY --from=builder /app/build/libs/fuploads-0.1.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
