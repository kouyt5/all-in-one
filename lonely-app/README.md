# 独立服务示例

这里保留原有 MySQL、Redis、FTP、FRP、Jupyter、MQTT、ELK、Squid、GitLab 等服务示例，供单独选用。它们不随根目录的首页服务启动，也没有在本次部署中启动。

除 code-server 外，各示例保留原来的镜像版本和结构；不代表这些历史版本已完成升级或生产验证。按需使用前请检查对应服务的配置、版本和端口。

## 本地凭据

- Compose 中原有明文密码已改为必填环境变量，例如 `MYSQL_ROOT_PASSWORD`、`REDIS_PASSWORD`。可以放在对应服务目录的 `.env` 中，该文件已被 Git 忽略。
- FRP 和 cloud 配置中的 `CHANGE_ME` 是脱敏占位符，使用前必须换成本机生成的凭据。不要将填写后的配置提交 Git。
- Jupyter 不再内置固定密码哈希。运行时设置 `JUPYTER_PASSWORD_HASH`，哈希生成方法见该目录说明。
- Squid 的 `squid/htpasswd` 和 MQTT 的 `auth.config/password_file` 已清空，未创建账号时无法使用密码认证。分别通过 `htpasswd`、`mosquitto_passwd` 创建本机账号后使用，不要提交生成的密码哈希。
- 本次修改前的原始凭据文件只在本机 `.secrets/lonely-app-originals/` 留有私密备份；历史中曾公开的有效凭据应在相应服务端轮换。

MQTT Compose 中公开 Docker 镜像标签的一行已标记为凭据扫描误报豁免；豁免不覆盖其他行或文件。
