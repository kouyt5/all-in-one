# v2ray配置

使用 v2ray 作为转发服务器，提供转发路由。只作为中继点，如果需要自行搭建梯子，需要在国外购买服务器搭建，搭建 vpn 不是本 app 的目的。

## 目的

想搭建一个在任何地点都能够翻墙的代理，云服务器是最好的选择，稳定运行，提供固定 ip。但是对于我来说，没有信用卡，就意味着不太好购买到国外的服务器，只能通过第三方 vpn 提供支持，使用国内服务器作为跳板实现翻墙。那问题来了，既然有了第三方的 vpn 为啥还要搭建一个国内的服务器呢，不是多此一举么。是这样的，其实在自己的 GUI 界面的操作系统下，第三方的工具已经能够满足要求，然而，有些时候遇到只能使用命令行操作的电脑或电脑上服务就比较麻烦了，这些好用简单的工具为了通用性，都是图形化的界面，没有 GUI 的电脑根本没法安装配置。所以不得不再配置一个简单的代理，输入地址就可以翻墙。刚刚说到，第三方 vpn 也会提供链接，但这个链接是专属的，只能由某些软件识别解析，因此不能直接使用，这个加密的链接解析出来是 v2ray 专属的配置，因此准备使用原生的 v2ray 结合这个配置再代理 http 流量，实现任何地点都能通过这个 http 链接 访问国外的网站。

## 搭建方法

使用 [googlehelper](http://googlehelper.net/)提供的手机代理工具实现国外网站的访问。 ghelper 本是一个浏览器代理工具，但是提供了 v2ray 订阅链接，为有 GUI 的操作系统包括手机、Linux系统、 windows 系统提供 vpn 支持，使用的第三方工具有 v2rayN、v2rayL 就可以使用这个链接。这个链接需要解析，原生的 v2ray 中似乎没发现解析的方法，因此借助第三方工具 v2rayN ,windows 下的图形化软件获取到配置信息。

1.  首先将订阅链接导入到 v2rayN 中
2.  去 v2rayN 的安装目录下，找到 `config.json` 文件，这个文件就是 v2ray 能够识别的文件
3.  在 `docker-compose.yml` 文件中修改端口号
4.  构建镜像 docker build -t v2ray\_localbuild:v0.1 .
5.  替换掉这个目录的 config.json 文件，启动 `docker-compose -f docker-compose-reg.yaml up` 就成功搭建了一个 docker 代理，注意 yaml 文件中的 config 文件
6.  如果想整点儿花里胡哨的，`docker stack deploy -c docker-compose.yml v2ray` 使用 swarm 启动一个docker 集群

## 配置文件解析

配置文件中有两个关键属性，**inbounds** 和 **outbounds**, inbounds 中包含关键的 http 代理，这个地方的属性关系到你能否通过 http 代理访问到国内的服务器，其中需要指明的是代理端口，这里使用 8889. outbounds 属性下就是连接国外 vpn 的关键配置，有软件生成后不需要更改。之前尝试自己配置但失败了，所以使用软件生成的比较靠谱。

## 使用方法

在 linux 命令行下，使用 `export http_proxy="ip:8888"` 就可以临时创建一个代理环境，终端退出后失效。不建议配置成固定的。当然也可以使用一些工具例如 wget 配合使用代理，不用设置export 环境变量。

## json 配置文件

    .
    ├── config-aws.json # AWS 云代理配置文件，目前部署失败，估计是由于墙的原因导致，因为使用 http 代理，未加密
    ├── config-cn2-usa.json # 翻墙代理配置文件
    ├── config-cn.json # 国内自建服务器代理配置文件，用于搭建校园代理
    ├── config.json # 翻墙代理配置文件
    ├── docker-compose-cn.yaml # 国内自建服务器代理搭建 docker-compose 启动文件
    ├── docker-compose-reg.yaml # 翻墙 docker-compose 启动文件
    ├── docker-compose.yml # 翻墙的 docker swarm 部署，但部署总会一段时间后失效，猜测是由于翻墙服务器拒绝访问导致的，因为长时间周期性的 `healthy check`

# 问题以及解决方法

git 设置代理 push 的时候报错：

    fatal: unable to access 'https://github.com/kouyt5/all-in-one.git/': Proxy CONNECT aborted

试了很多方法都不行，比如换成 `git config` 配置，查了很久，问题是因为代理使用了认证导致的，需要 git 里设置一下：

    git config --global http.proxyAuthMethod 'basic'
