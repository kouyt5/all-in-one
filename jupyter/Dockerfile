FROM jupyter/datascience-notebook
ENV NB_PREFIX=/jupyter/

USER root
# 挂载权限
RUN mkdir -p /usr/local/bin \
  && wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/1.10/gosu-amd64" \
  && chmod +x /usr/local/bin/gosu

COPY entrypoint.sh /entrypoint.sh
RUN chmod a+x /entrypoint.sh && chown 1000:100 /entrypoint.sh
ENTRYPOINT [ "/entrypoint.sh" ]
CMD ["sh","-c", "start.sh jupyter lab --ip=0.0.0.0 --no-browser --allow-root --port=8888 \
 --NotebookApp.password='sha1:8afe621e8f22:51015a88aadb5a9802be3aaa8425e797fccb8666' \
  --NotebookApp.allow_origin='*' \
#  --NotebookApp.token='' \
 --notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"] 
# 以下是无密码配置
# CMD ["sh","-c", "jupyter notebook --ip=0.0.0.0 --no-browser --allow-root --port=8888 \
#  --NotebookApp.password='' --NotebookApp.allow_origin='*' \
#  --NotebookApp.token='' \
#  --notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"]