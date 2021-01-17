# 需要满足驱动大于440
FROM nvcr.io/nvidia/pytorch:20.01-py3

ENV PASSWORD 511023
ENV folder /root/code
WORKDIR /workspace
# release 3.2.0
RUN wget -c https://github.com/cdr/code-server/releases/download/3.2.0/code-server-3.2.0-linux-x86_64.tar.gz \
    && tar -xvf code-server-3.2.0-linux-x86_64.tar.gz \
    && cp -r code-server-3.2.0-linux-x86_64 code-server \
    && rm -rf code-server-3.2.0-linux-x86_64* 

EXPOSE 80
ENTRYPOINT ./code-server/code-server --host 0.0.0.0 --port 80 --auth password&& /bin/bash
