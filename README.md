# all-in-one

个人首页、文件浏览与上传服务。根部署只包含 Nginx 和 Kotlin / Spring Boot 上传 API；code-server 按需启用。

## 结构

- `nginx/src/app/`：React + Ant Design 前端，Vite 构建。
- `nginx/src/fuploads/`：上传 API，Java 21 运行时；无需 MySQL 或 FTP。
- `nginx/conf.d/`：网关、HTTPS、上传认证和下载安全策略。
- `scripts/`：本机凭据初始化和部署集成测试。
- `lonely-app/`：保留各独立服务示例；code-server 为根部署可选 IDE，其他服务按需单独启动。
- `.secrets/`：本地生成的凭据和证书，禁止提交。
- `swarm/single-box/`、`swarm/v2ray/`：两套独立代理部署入口和脱敏模板，不属于首页部署；真实配置位于 `.secrets/`。

## 部署

需要 Docker Engine、Docker Compose、Python 3 和 OpenSSL。先替换下面的公网 IP：

```bash
python3 scripts/init-secrets.py --ip YOUR_PUBLIC_IP
COMPOSE_PARALLEL_LIMIT=1 docker compose build
# 默认不启动 code-server，优先保障首页和上传。
docker compose up -d --wait
python3 scripts/smoke-test.py
```

首页：`http://YOUR_PUBLIC_IP/`。上传入口：`https://YOUR_PUBLIC_IP/upload/#/file-upload/`。
无域名部署使用自签名证书，浏览器首次打开 HTTPS 页面会提示不受信任；请核验证书指纹或将证书导入可信列表。`openssl x509 -in .secrets/server.crt -noout -fingerprint -sha256` 可查看指纹。对公网开放 TCP 80 和 443。

上传账户保存在 `.secrets/upload_credentials`，仅本机所有者可读；不要将内容粘贴进聊天、日志或提交。上传页面和 API 强制 HTTPS 并要求 Basic 认证；首页和下载公开。上传的数据会公开供下载，请勿上传私密文件。

容器日志轮转；Nginx 内存上限 96 MiB，API 448 MiB、JVM 堆 192 MiB。构建串行执行以减少内存峰值。可选 IDE 上限 512 MiB：

```bash
docker compose --profile ide up -d code-server
# IDE 仅绑定服务器回环地址，通过 SSH 隧道使用：
ssh -L 8081:127.0.0.1:8081 USER@YOUR_PUBLIC_IP
```

然后打开本机 `http://127.0.0.1:8081`，密码见 `.secrets/code_password`。如需在首页展示 IDE，将 `.env` 中 `CODE_SERVER_ENABLED=true` 并重建 nginx，启动后可通过 HTTPS `/code/` 访问；IDE 保留自身密码认证。

## 上传接口与安全边界

`POST /api/uploads`，multipart 字段 `file`，可多文件，最多 20 个；可选 `path` 指定上传根目录内的子目录，默认 `/`。响应保留 `code/message/data.successFileList/data.failedFileList`，无效文件返回 HTTP 400，超限返回 413。批量上传允许部分成功，返回成功和失败文件列表。

单次请求最大 100 MB。上传流式写盘，拒绝空文件、隐藏路径、路径穿越、符号链接和同名覆盖。ZIP/TAR 自动解压，总解压量限制为 256 MiB、最多 2000 个条目；拒绝压缩包链接和特殊文件。失败的单文件操作清理暂存内容。ZIP 中央目录最大 1 MiB，不支持分卷和 ZIP64；7z 不自动解压。

文件写入 `all-in-one_uploads` Docker 卷，重启保留；Nginx 只读挂载，PDF、TXT、常见音视频和位图使用 inline 在线查看；其他类型（包括 HTML/SVG）使用 attachment 和 CSP sandbox，避免上传内容在站点权限下执行。媒体支持 HTTP Range；能否播放取决于浏览器支持的编码。`.tmp` 目录不可公开访问。保留至少 512 MiB 磁盘余量；仍需按使用量清理文件、监控磁盘及备份数据。

```bash
# 备份到当前目录（备份内容不提交 Git）
mkdir -p .local/backups
docker run --rm -v all-in-one_uploads:/data:ro -v "$PWD/.local/backups:/backup" alpine:3.23 tar -czf /backup/uploads.tar.gz -C /data .
```

不要运行 `docker compose down -v`，该命令会删除数据卷。

## 测试与凭据处理

镜像构建执行前后端测试；`scripts/smoke-test.py` 验证部署后的认证、上传下载、压缩包解压与恶意输入。CI 同时运行测试和构建，不再自动操作固定服务器路径或全局清理 Docker。

独立服务示例保留在 `lonely-app/`，相关凭据已脱敏；根部署和上传后端已移除 FTP/MySQL/Jupyter 依赖。曾提交的密码、代理令牌、连接信息即使从当前文件删除，仍可能存在于 Git 历史或远程副本中，应在相应服务轮换。清理当前工作区不会改写 Git 历史；保留的代理真实配置已迁入 `.secrets/`，历史代理凭据仍应在相应服务端轮换。
