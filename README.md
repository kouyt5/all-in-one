# code-server-docker
docker-compose 部署nginx与code-server
# 当前工作
之前想使用nginx代理计算机上所有开放端口，但是在使用过程中发现code-server的不能正常代理（能够访问登录页面但是无法进入code）后面思考，code-server的应用场景不是随时启动，重新部署也对应用无影响的场景，需要持续使用容器环境，一旦重新构建，所有环境需要重新搭建，显然不符合需求。虽然代码可以保存，但是根据需求安装的环境却是随着镜像的重构而重构。所以只能使用Dockerfile预构建满足深度学习的pytorch镜像。不适用docker-compose方式。
# 启动方式
预先构建镜像
```bash
docker build --name [image name] .
```
创建永久运行的容器
```
docker run -it -d -gpus all -p 8080:80 -v app/:/root/code [image name]
```
访问 http://localhost:8080 输入 `Dockerfile` 中配置的密码。登录code-server

# 注意事项
+ 基础镜像来着nvidia 的 ![NGC](https://ngc.nvidia.com/setup) ,简单说就是需要到 NGC 注册才能够获取镜像下载权限，然后才能构建镜像。
+ 支持深度学习的镜像一般需要安装 ![nvidia-docker](https://github.com/NVIDIA/nvidia-docker)才能连接到宿主机的nvidia驱动
+ 目前的镜像需要硬件支持，nvidia 显卡驱动大于*440*
