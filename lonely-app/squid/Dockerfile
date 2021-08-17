FROM ubuntu:18.04

COPY apt-sources18.list /etc/apt/sources.list
RUN apt update
RUN apt-get install -y tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apt-get install -y -f squid
VOLUME [ "/var/spool/squid/", "/var/log/squid/" ]
COPY ./chown.sh /chown.sh
RUN chmod a+x /chown.sh
