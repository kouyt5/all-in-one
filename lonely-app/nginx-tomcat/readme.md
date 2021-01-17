# tomcat与nginx组合
nignx与tomcat的组合，nginx用于配置https和路由功能，tomcat负责服务器。
+ 首先，建立weapp目录，将war包放入weapp
+ 新建ssl文件夹，用于存放https证书，full_chain.pem公钥和private.key私钥
+ 启动项目
```
docker-compose up
```