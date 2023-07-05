# tomcat 与 nginx 组合

nignx 与 tomcat 的组合，nginx 用于配置 https 和路由功能， tomcat 负责服务器。
+ 首先，建立 weapp 目录，将 war 包放入 weapp
+ 新建 ssl 文件夹，用于存放 https 证书，full_chain.pem 公钥和 private.key 私钥
+ 启动项目

```
docker-compose up
```