# sing-box（目录名保留 single-box）

仓库只保存 Compose 和脱敏配置模板。真实配置位于项目根目录 `.secrets/sing-box/config.json`，不会提交 Git。现有连接信息迁移时原样保留。

首次配置（从仓库根目录执行；已有真实配置时不要覆盖）：

```bash
install -d -m 700 .secrets/sing-box
# 仅在文件不存在时初始化，避免覆盖已有凭据。
test -e .secrets/sing-box/config.json || install -m 600 swarm/single-box/config.example.json .secrets/sing-box/config.json
```

编辑真实配置，将 `proxy.example.com`、示例 UUID、`CHANGE_ME` 替换为节点信息。模板默认校验证书；不要提交真实服务器地址、密码和 UUID。

```bash
docker compose -f swarm/single-box/docker-compose.yml run --rm --no-deps sing-box check -c /etc/sing-box/config.json
docker compose -f swarm/single-box/docker-compose.yml up -d
```

SOCKS：`127.0.0.1:11400`；HTTP：`127.0.0.1:11401`。仅绑定本机。镜像与当前已验证版本保持一致并固定摘要，内存上限 200 MiB。不会随首页 Compose 一起启动。
