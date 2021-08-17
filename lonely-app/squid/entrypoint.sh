#!/bin/sh

echo "改变目录权限"
chown -R proxy:proxy /var/spool/squid/
chown -R proxy:proxy /var/log/squid/
echo "创建缓存目录"
squid -z
echo "开始squid服务..."
# entrypoint会续接cmd命令，例如./entrypoint.sh squid -NX, squid -NX为cmd命令。
# 用下面的代码表示将entrypoint后面的参数传入进来执行。
exec "$@"