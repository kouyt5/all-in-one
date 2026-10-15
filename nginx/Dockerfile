FROM node:24-alpine@sha256:44fce696f38ba8c987c4db89954051000bbd05ffd8d8659ba15ed4a8602e937f AS builder
WORKDIR /app
ENV NODE_OPTIONS=--max-old-space-size=768 GENERATE_SOURCEMAP=false
COPY src/app/package*.json ./
RUN npm ci --no-audit --no-fund
COPY src/app/ ./
ARG VITE_ENABLE_CODE_SERVER=false
ENV VITE_ENABLE_CODE_SERVER=$VITE_ENABLE_CODE_SERVER
RUN npm test && npm run build
FROM nginx:stable-alpine@sha256:970364228b1e4f932abc22d321932816782cca8a20b85a77552a7cfe9acfb3b5
COPY --from=builder /app/build/ /home/www/web/
