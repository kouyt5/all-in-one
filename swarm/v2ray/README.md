# V2Ray 单实例

只保留一份部署配置，作为 sing-box 的独立备选；不默认启动。

- `docker-compose.yml`：官方镜像，无额外 Dockerfile。
- `config.example.json`：脱敏的 VMess + WebSocket + TLS 示例。
- 真实配置：根目录 `.secrets/v2ray/config.json`，由原 `config.json` 迁移；保留原节点参数，日志改为容器输出。

旧的多地区、多协议配置和原始文件已移出此目录，仅在本机 `.secrets/proxy-backups/v2ray/` 保留备份，不提交 Git。旧的 `config-out.json` 混用了 sing-box 和 V2Ray 格式，未作为有效配置保留。

首次配置（从根目录执行，已有真实配置时不要覆盖）：

```bash
install -d -m 700 .secrets/v2ray
test -e .secrets/v2ray/config.json || install -m 600 swarm/v2ray/config.example.json .secrets/v2ray/config.json
```

编辑真实配置后校验并启动：

```bash
docker compose -f swarm/v2ray/docker-compose.yml run --rm --no-deps v2ray test -c /etc/v2ray/config.json
docker compose -f swarm/v2ray/docker-compose.yml up -d
```

本机 SOCKS：`127.0.0.1:12400`；HTTP：`127.0.0.1:12401`，可用 `V2RAY_SOCKS_PORT`、`V2RAY_HTTP_PORT` 覆盖宿主机端口。避免与 sing-box 的 11400/11401 冲突。内存上限 200 MiB。

模板不含有效节点。配置校验不代表原远程节点当前可连接；原历史凭据如已公开，应在节点服务端轮换。
