from ubuntu:18.04

COPY apt-sources18.list /etc/apt/sources.list
RUN apt update
RUN apt-get install -y tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apt-get install -y -f squid
