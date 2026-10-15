FROM jupyter/datascience-notebook:2023-06-03
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
 --NotebookApp.password=\"${JUPYTER_PASSWORD_HASH:?Set JUPYTER_PASSWORD_HASH}\" \
  --NotebookApp.allow_origin='*' \
#  --NotebookApp.token='' \
 --notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"]
# 以下是无密码配置
# CMD ["sh","-c", "jupyter notebook --ip=0.0.0.0 --no-browser --allow-root --port=8888 \
#  --NotebookApp.password=\"${JUPYTER_PASSWORD_HASH:?Set JUPYTER_PASSWORD_HASH}\" --NotebookApp.allow_origin='*' \
#  --NotebookApp.token='' \
#  --notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"]
