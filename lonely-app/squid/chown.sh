#!/bin/sh

echo "改变目录权限"
chown -R proxy:proxy /var/spool/squid/
chown -R proxy:proxy /var/log/squid/
echo "创建缓存目录"
squid -z
echo "开始squid服务..."
squid -N