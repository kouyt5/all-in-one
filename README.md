# all-in-one
目前就取个这个名字吧，意味着所有的东西都在一个平台上管理。
## 解决问题是什么？
遇到一件系统性的麻烦事。就是在计算机刚开始使用时候，搭建各种各样的服务，因为搬砖需要，也会搭建各种代码的运行环境，然而，经常是这个环境用一段时间就搁置在那里了，很久之后就搞忘了环境到底安装了什么东西？当然这不是致命的，因为可以重新创建环境就行，然而如果在电脑上安装了很多服务，例如ftp、mysql等，大部分服务需要修改配置文件，如果很久时间不碰了，很难记清楚搭建的步骤、到底在哪里修改了什么文件等等等等，当然，如果比较勤快的话可以做一个记录，每一步的操作都记下来，可这种低级的事情真不想做，首先如果细节比较多的话非常麻烦，而且很容易漏忘什么东西。当然你可以说我比较细致，这些no problem，然而如果你的电脑崩了呢，按照记录的教程一步步搭建将会非常麻烦，你可以说我不怕麻烦，但是如果你的电脑拿给别人用呢，别人也看你的教程么，也可以，但不专业，很麻烦。（注意：这些麻烦事都是在服务器上，自己用的笔记本应该不需要安装各种服务）

## 解决方案
前段时间一直在使用了解 `docker`，docker 可以使得环境完全可视化，只要按照规矩来，不要在容器内直接操作环境，那么就能保证环境的可复现性。但只使用docker只能作为环境隔离工具，达不到管理需求，因此准备使用 `k8s` 为计算机搭建一个电脑资源可视化的平台，管理所有的应用或者服务。因此，对于开发来说，将导致所有的代码将运行在docker之上，这个方案经过验证是可行的。然后是其他的服务也使用docker创建，现在docker比较成熟，各种应用几乎都包含 docker，也具有可行性，当然，可能按照自己的意愿搭建一个个服务有些困难需要克服，但是我们可以一步步学习嘛，总能解决的。

安装 `jupyter` 的时候意外看到一个网站，https://www.kubeflow.org/ ，这个`Kubeflow` 的想法跟我要做的事情比较像，管理机器学习应用，不过这是生产级别的，运行在k8s之上，对模型的生命周期做了一个管理。 后期可以尝试应用。但留给我的时间不多了，哎~

之前也听说过 `nginx` 反向代理，代理http应用，想着就可以结合`code-server` 在网页上开发，岂不是美滋滋。使用nginx还可以节约端口，不需要在计算机上另开一个端口，就不用设置内网穿透了，还得记住端口，久了也要搞忘。 nginx 配置还是踩了很多坑，看来自己的能力还是真的撑不起自己的野心。

总结一下，目前支持的服务：
+ `jupyter lab` 一个专门用来科学计算的工具
+ `nginx` 服务器 作为所有服务的代理
+ `code-server` 一个网页上运行的 `vscode`
+ `ftp` 服务器，用于文件本地上传，再使用 `nginx` 作为文件查看服务器，这样做的原因是 http 可以直接在线查看pdf，ftp不行。
+ `v2ray` 翻墙**代理**服务器设置（非VPN，需要预先有国外服务器或第三方服务，详情见 [lonely-app/v2ray/README.md](lonely-app/v2ray/README.md) ）
+ `tomcat` 支持(暂时未部署应用,经验证可行)
+ `portainer` 容器管理平台
+ `bitwarden` 个人密码管理

后续将会不断完善。
## 安装步骤
环境依赖：（注意，计算机上只需要安装这两个应用，然后所有服务都不需要其他的任何配置，这就是docker的强大之处）
+ `docker` ,`docker-compose` 这两个是基本的应用，安装在宿主机，安装步骤自行百度或者官网 https://docs.docker.com/engine/install/ubuntu/

启动步骤(在华为云服务器上部署(2核4G))

**0**. clone 代码。因为nginx默认开启ssl，因此需要自行根据需求，把ssl的关键文件放入 nginx 目录下，如
```
# 免费 ssl 证书申请 https://freessl.cn/
nginx/ssl/
├── full_chain.pem
└── private.key
```
如果没有证书，注释掉 nginx/conf.d/default.conf 文件中关于ssl的部分。

**1**. jupyter 和 code-server 都需要配置密码，jupyter 的密码配置在jupyter/Dockerfile 中，code-server 在docker-compose.yml 中。jupyter 的配置请看 jupyter 目录下的 [jupyter/README.md](jupyter/README.md) 文件。当然你可以忽略掉密码配置部分，但这两个服务都必须输入密码登录的，可以后面回过头再看。nginx默认监听80端口，所以请确保80端口对外开放。

**2**. 在一台裸机上安装 docker,docker-compose 两个 docker 环境运行基础

**3**. 进入项目根目录，输入`docker-compose up` 就可以直接启动nginx关联服务（包括jupyter、codlab、nginx）

**4**. 根据需求，到 lonely-app 目录下分别使用 docker-compose up 启动相应服务。（例如portainer、bitwarden非必须）

然后打开浏览器，输入 `http://ip/` 就可以访问到主页。
## 当前工作
+ 使用 `docker-compose` 集成多个应用，包括nginx代理、code-server、jupyter lab、nginx文件服务器。
+ 搭建了tomcat、机器学习code-server、ftp服务器等应用。

文件夹含义：
```
|-- app                 # 每个应用的需要储存的文件
|   |-- code-server
│   ├── juypter         # jupyter容器产生的文件，防止重新启动容器信息丢失
│   └── www             # 存放nginx 文件
|-- code-server         # code-server 服务
|-- docker-compose.yml  # docker-compose文件
|-- jupyter             # jupyter 配置
│   ├── Dockerfile      
│   ├── entrypoint.sh   # 权限相关
|-- lonely-app          # 单体应用，不包含到集群
│   ├── code-server     # 单个机器学校pytorch 和cuda环境
│   ├── ftp             # ftp 注意，nginx需要ftp的文件夹，因为nginx不提供文件上传功能
│   └── tomcat          # tomcat服务器
|-- nginx               # nginx代理配置
│   ├── conf.d
│   ├── ssl             # 证书配置
│   └── web             # 简单 web 页面
`-- speedup             # 加速源
```
## TODO

[ ] vscode-server 添加外部端口支持，为web应用提供访问: 端口11400-11401
[x] 实现springBoot Security微服务分布式部署登录
## bug
1. java安装完成，但运行始终报错 `/root/.local/share/code-server/extensions/redhat.java` 说是无法makedir。解决方案是清理一下Java language server workspace ，或者删掉一些项目无关的文件，重启界面。
2. 权限问题，很多容器启动其中的服务使用非root用户，导致在docker-compose.yml 中默认的root用户创建的目录无写入权限，可以挂载到服务启动用户的用户目录规避这个问题，也可以更改挂载目录权限。
## 常用命令
清理docker无用容器或镜像
```
docker system prune
```