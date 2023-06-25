FROM gradle:7-jdk as builder
USER root
WORKDIR /app
# gradle 加速: https://juejin.cn/post/6844904072990949390
COPY init.gradle /root/.gradle/
# Copy all files
COPY ./ /app/
RUN ./gradlew clean build --no-daemon -x test

FROM openjdk:17-jdk-alpine
WORKDIR /apps
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

EXPOSE 8080
COPY --from=builder /app/build/libs/fuploads-0.1.jar ./

ENTRYPOINT ["java", "-jar", "fuploads-0.1.jar"]
CMD [ "" ]