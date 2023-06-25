FROM node:18-alpine as builder

WORKDIR /app

COPY src/app/ /app/
RUN npm install && npm run build

FROM nginx:1.15

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

COPY --from=builder /app/build/ /home/www/web/

