# 简介
有些事情可能不能做得太绝对，这里还是放一些单体应用docker构建方法。
## 深度学习code-server
### 使用Dockerfile构建
构建支持pytorch的深度学习code-server
```bash
docker build --name [image name] .
```
创建永久运行的容器
```
docker run -it -d -gpus all -p 8080:80 -v app/:/root/code [image name]
```
访问 http://localhost:8080 输入 `Dockerfile` 中配置的密码。登录code-server
### 使用docker-compose
直接在根目录下输入如下命令
```bash
docker-compose up -d
```
就能永久启动一个服务，具体参数配置详见docker-compose.yml文件
## 注意事项
+ 基础镜像来着nvidia 的 [NGC](https://ngc.nvidia.com/setup) ,简单说就是需要到 NGC 注册才能够获取镜像下载权限，然后才能构建镜像。
+ 支持深度学习的镜像一般需要安装 [nvidia-docker](https://github.com/NVIDIA/nvidia-docker)才能连接到宿主机的nvidia驱动
+ 目前的镜像需要硬件支持，nvidia 显卡驱动大于*440*
