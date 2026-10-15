# 上传 API

Java 21、Gradle Wrapper。`./gradlew test bootJar` 构建和测试；`java -jar build/libs/fuploads-0.1.jar` 启动。默认监听 8080，UPLOAD_ROOT 指定持久化目录。生产部署通过根 Compose 提供 HTTPS 和认证，不要单独将 API 端口发布公网。
