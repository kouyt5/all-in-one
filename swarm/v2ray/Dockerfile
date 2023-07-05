FROM v2fly/v2fly-core:v4.45.2

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

# 添加国内源
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.ustc.edu.cn/g' /etc/apk/repositories
# 安装curl
RUN apk update && apk add curl && rm -rf /var/cache/apk/*