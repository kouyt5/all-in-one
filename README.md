# all-in-one

![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kouyt5/all-in-one/di.yml?label=%E9%83%A8%E7%BD%B2)
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kouyt5/all-in-one/gradle.yml?label=%E6%8E%A5%E5%8F%A3)
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/kouyt5/all-in-one/node.js.yml?label=%E9%A1%B5%E9%9D%A2)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1fe44958cf4546c38bcdc5aa7542ba13)](https://app.codacy.com/gh/kouyt5/all-in-one/dashboard?utm_source=gh\&utm_medium=referral\&utm_content=\&utm_campaign=Badge_grade)

基于 docker 封装的各种开源组件或服务，方便开箱即用。同时包含一个集成`文件浏览`、`code-server`、`Jupyter-Lab` 和`文件上传`的网页服务。

## Q\&A

**项目目标 ?**

该项目库没有太多额外的代码 (python, Java, etc) ，主要目的是给开源组件或者服务(例如 **mysql**, **ftp**, **rabbitmq**, **tomcat** 等)封装了方便启动并且风格统一的 Dockerfile ,然后使用 `docker compose` 技术启动

**仓库结构**

项目有两个功能，首先是最可能用到的**单个服务集合**，例如您想启动一个 mysql ，直接到 lonely-app/mysql 下输入 `docker-compose up -d` 即可。另一个是额外附带一个 **nginx 代理的网关**，网关下包含一些有用的服务，例如 code-server 和文件服务器。在根目录输入`docker-compose up -d` 启动，两者互相不冲突。这个服务作为作者的日常开发使用已经稳定部署在云服务器上。

项目的文件夹说明如下：

    .
    ├── app # 产生的永久文件
    ├── lonely-app # 包含单个服务，单独在其文件夹下运行docker-compose up 启动
    ├── nginx # 应用集成网关
    ├── speedup # 加速源
    └── swarm # 一些swarm集群例子(deprecated)

## 包含的服务：

*   经过 nginx 代理的服务：
    *   `jupyter lab` 一个专门用来科学计算的工具
    *   `nginx` 服务器 作为所有服务的代理
    *   `code-server` 一个网页上运行的 `vscode`
    *   文件上传页面（react + SpringBoot(kotlin)）

*   单个服务，单独启动运行。在 📁 lonely/ 目录或者 swarm 下
    *   `ftp` 服务器，用于文件本地上传，再使用 `nginx` 作为文件查看服务器，这样做的原因是 http 可以直接在线查看 pdf ， ftp 不行。
    *   `v2ray` **代理**服务器设置（非 VPN ，翻墙需要预先有国外服务器或第三方服务，详情见 [swarm/v2ray/README.md](swarm/v2ray/README.md) ）
    *   `tomcat` 包括 `swarm` 下的集群配置
    *   `elasticsearch + Kibana + logstash` 环境搭建
    *   `cloudrev` 类似于百度云
    *   `mysql` mysql5.7 和 mysql8
    *   `rabbitmq` 消息队列服务
    *   `squid` http 代理服务器
    *   `frp` 反向代理，用作内网穿透
    *   `gitlab` 类似 GitHub 的代码托管网站
    *   `nginx-tomcat` nginx 和tomcat 的联合，nginx 用作网关
    *   `bitwarden` 个人密码管理
    *   `redis`
    *   `squid` 代理服务器
    *   `mqtt` mqtt 服务器，使用 mosquitto 作为权限模块

后续将会不断完善。

## 环境依赖

*   `docker`
*   `docker-compose`

这两个是基本的应用，安装官网链接： https://docs.docker.com/engine/install/ubuntu/

## 单个服务启动方法

到 📁 lonely-app 目录下使用 `docker-compose up` 命令启动对应应用即可，例如到 `lonely-app/rabbitmq` 目录下输入：

    docker-compose up -d

即可搭建一个 rabbitmq 的开发环境。部分环境例如 ftp 可能会依赖于环境变量来设置密码，如果需要，请先命令行设置环境变量。

## 基于 nginx 服务器的 https 代理网关搭建

<img alt="主页" src="assets/homepage.jpg" height="400">

如果想搭建一个如上图所示的网页，用于访问 code-server 、nginx 的文件服务器、jupyter-lab 等请跟随下面的步骤完成。

### 密码设置

本项目中某些服务可能需要配置密码，密码信息通常在 `docker-compose.yml` 进行配置。但密码属于敏感信息，不能直接提交到 git 仓库，为了便于代码提交，部分服务使用读取环境变量的方式来设置密码。可以看到部分 `docker-compose.yml` 中 environment 下有 `pass=${FTP_PASS}` 项，这种 `docker-compose.yml` 文件的启动就需要设置环境变量。

启动该 nginx 网关所需的环境变量如下：

```bash
export CODE_SERVER_PASSWORD=password
export FTP_PASS=password
export PASV_ADDRESS=ip
```

建议将其放在 `~/.bashrc` 文件的末尾

### 启动步骤

**1️⃣**. 因为 nginx 默认开启 ssl 即 https ，因此需要自行根据需求，把 ssl 的关键文件放入 nginx 目录下，如

    # 免费 ssl 证书申请 https://freessl.cn/
    nginx/ssl/
    ├── full_chain.pem
    └── private.key

如果没有证书，注释掉 nginx/conf.d/default.conf 文件中关于ssl的部分。

示例如下:

    server {
        listen        8080;
        server_name xxxx.com;
        # ssl_certificate /ssl/full_chain.crt; #证书路径
        # ssl_certificate_key /ssl/private.key; #key路径
        auth_basic  on;
        charset utf-8;
        underscores_in_headers on; # 默认的情况下nginx引用header变量时不能使用带下划线的变量。需要配置underscores_in_headers on
        ...

**2️⃣**. jupyter 还需要额外配置密码，jupyter 的密码配置在 📁 jupyter/Dockerfile 中，具体请看 jupyter 目录下的 [jupyter/README.md](/lonely-app/jupyter/README.md) 文件。

**3️⃣**. 进入项目根目录，输入 `docker-compose up --build` 就可以直接启动nginx关联服务（包括jupyter、jupyterlab、nginx）

**4️⃣** 然后打开浏览器，输入 `http://ip/` 就可以访问到主页✅

> **⚠️** **nginx默认监听80端口，所以请确保80端口对外开放。**

#### 项目网页打包原理

本项目中，通过 `docker-compose up --build` 将会构建所需的 docker 镜像并完成部署。开源服务如 `jupyter`、`coder-server` 都有现成的镜像使用，本文只是对其进行简单的配置。对于网页，本文基于 `React` 框架进行构建，在项目构建时，首先其会通过 `npm` 进行打包，然后将打包的文件放到 `nginx` 的静态资源文件夹下，从而完成网页的部署。后端服务同理，通过 `gradle` 进行项目打包，然后运行在虚拟机中对外提供服务。网页源码和后端源码在 📁 `nginx/src` 目录下。对于文件浏览，本项目使用 `nginx` 进行简单的配置，具体请参考 `nginx` 配置文件。

在本项目中，使用 GitHub 的 Action 功能实现项目的自动部署。首先然后在一个服务器💻上部署了一个 `Self-Host Runner`，代码提交或合并到 master 分支会触发 Action，从而完成部署。

## last

*   docker 不是万能的，要考虑到容器技术的发展，不少应用不太好迁移到 docker 中去，使用 docker 之前最好就要考虑到代码将在 docker 运行，这样构建的应用或服务才能充分利用 docker 带来的便利。
*   docker 的特性使得不太适合 mysql 这种需要持久化的应用， docker 的服务最好是无状态的那种。
*   迁移到 docker 这个过程带来的时间成本可能比你想象中的要多，但是同时这样做带来的便利真如你想象的那样。
