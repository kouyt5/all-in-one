#!/bin/sh

chown -R jovyan:100 /workdir
chown -R jovyan:100 /opt/conda/share/jupyter/lab
chown -R jovyan:100 /home/jovyan/.jupyter/lab
gosu jovyan "$@"