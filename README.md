# all-in-one
基于 docker 封装的各种开源组件，方便开箱即用。
## Q&A

**contribution ?**

秉承代码搬运工原则，该项目库没有太多额外的代码(python, Java, etc)，主要目的是给开源组件或者服务(例如 **mysql**, **ftp**, **rabbitmq**, **tomcat** 等)封装了方便启动并且风格统一的 Dockerfile ,然后使用 `docker-compose` 技术启动

**why use docker?**

docker 技术保证了环境的隔离和可复现性，正常情况下能够很方便的迁移或者安装到不同的环境下。

**structure of the repository**

项目有两个功能，首先是最可能用到的**单个服务集合**，例如您想启动一个 mysql，直接到 lonely-app 下输入 `docker-compose up -d` 即可。另一个是额外附带一个**nginx 代理的网关**，网关下包含一些有用的服务，例如 code-server 和文件服务器。在根目录输入`docker-compose up -d` 启动，两者互相不冲突。这个服务作为作者的日常开发使用已经稳定部署在云服务器上。

项目的文件夹说明如下：
```
.
├── app # 产生的永久文件
├── lonely-app # 包含单个服务，单独在其文件夹下运行docker-compose up 启动
├── nginx # 应用集成网关
├── speedup # 加速源
└── swarm # 一些swarm集群例子
```


## 包含的服务：

+ 经过nginx代理的服务：
  + `jupyter lab` 一个专门用来科学计算的工具
  + `nginx` 服务器 作为所有服务的代理
  + `portainer` 容器管理平台 （被遗弃）
  + `code-server` 一个网页上运行的 `vscode`
  + 文件上传页面（react + SpringBoot(kotlin)）

+ 单个服务，单独启动运行。在lonely/目录或者swarm下
  + `ftp` 服务器，用于文件本地上传，再使用 `nginx` 作为文件查看服务器，这样做的原因是 http 可以直接在线查看pdf，ftp不行。
  + `v2ray` **代理**服务器设置（非VPN，翻墙需要预先有国外服务器或第三方服务，详情见 [swarm/v2ray/README.md](swarm/v2ray/README.md) ）
  + `tomcat` 包括`swarm` 下的集群配置
  + `elasticsearch+Kibana+logstash` 环境搭建
  + `cloudrev` 类似于百度云
  + `mysql` mysql5.7和mysql8
  + `rabbitmq` 消息队列服务
  + `squid` http代理服务器
  + `frp` 反向代理，用作内网穿透
  + `gitlab` 类似GitHub的代码托管网站
  + `nginx-tomcat` nginx和tomcat的联合，nginx用作网关
  + `bitwarden` 个人密码管理

后续将会不断完善。
## 环境依赖
+ `docker`
+ `docker-compose`

这两个是基本的应用，安装官网链接： https://docs.docker.com/engine/install/ubuntu/

## 单个服务启动方法

到lonely-app目录下使用 `docker-compose up` 命令启动对应应用即可，例如到 `lonely-app/rabbitmq`目录下输入：
```
docker-compose up -d
```
即可搭建一个rabbitmq的开发环境。部分环境例如 ftp 可能会依赖于环境变量来设置密码，如果需要，请先命令行设置环境变量。

## 基于 nginx 服务器的 https 代理网关搭建

<img alt="主页" src="assets/homepage.jpg" height="400">

如果想搭建一个如上图所示的网页，用于访问 code-server 、nginx 的文件服务器、jupyter-lab 等请跟随下面的步骤完成。

### 密码设置
密码基于环境变量的方式配置。主要有两个密码在 `docker-compose.yml` 中设置，code-service 和 ftp 的密码。因此需要设置如下三个环境变量：
```bash
export CODE_SERVER_PASSWORD=password
export FTP_PASS=password
export PASV_ADDRESS=ip
```
可以将其放在`~/.bashrc` 文件的末尾，也可以直接在 shell 中输入，不过只能对当前登录的 shell 有效
### 启动
**0**. 因为 nginx 默认开启 ssl 即 https ，因此需要自行根据需求，把 ssl 的关键文件放入 nginx 目录下，如
```
# 免费 ssl 证书申请 https://freessl.cn/
nginx/ssl/
├── full_chain.pem
└── private.key
```
如果没有证书，注释掉 nginx/conf.d/default.conf 文件中关于ssl的部分。

**1**. jupyter 和 code-server 都需要配置密码，jupyter 的密码配置在jupyter/Dockerfile 中，code-server 在docker-compose.yml 中。jupyter 的配置请看 jupyter 目录下的 [jupyter/README.md](/lonely-app/jupyter/README.md) 文件。当然你可以忽略掉密码配置部分，但这两个服务都必须输入密码登录的，可以后面回过头再看。nginx默认监听80端口，所以请确保80端口对外开放。

**2**. 进入项目根目录，输入`docker-compose up --build` 就可以直接启动nginx关联服务（包括jupyter、jupyterlab、nginx）

然后打开浏览器，输入 `http://ip/` 就可以访问到主页。主页的html代码在nginx目录下。
## last

+ docker 不是万能的，要考虑到容器技术的发展，不少应用不太好迁移到docker中去，使用docker之前最好就要考虑到代码将在docker运行，这样构建的应用或服务才能充分利用docker带来的便利。
+ docker的特性使得不太适合mysql这种需要持久化的应用，docker的服务最好是无状态的那种。
+ 迁移到docker这个过程带来的时间成本可能比你想象中的要多，但是同时这样做带来的便利真如你想象的那样。