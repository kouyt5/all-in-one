+ frp版本: 0.37.0

# 启动
## 启动服务端

```
./frps -c ./frps.ini
```
or 使用docker
```
docker-compose up -d
```
其中frps.ini文件如下
```
[common]
bind_port = 7000  # 客户端连接的端口
vhost_http_port = 7600  # 用于配置http
dashboard_port = 7500  # 管理界面
# dashboard的用户名和密码
dashboard_user = user
dashboard_pwd = pass
```

## 启动客户端

```
./frp -c ./frp.ini
```
or 使用docker
```
docker-compose -f docker-compose-client.yml up -d
```