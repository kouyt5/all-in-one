FROM ubuntu:18.04

COPY apt-sources18.list /etc/apt/sources.list
RUN apt update
RUN apt-get install -y tzdata && ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apt-get install -y -f squid
VOLUME [ "/var/spool/squid/", "/var/log/squid/" ]
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh"]
