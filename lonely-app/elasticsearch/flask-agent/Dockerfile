FROM python:3.7

COPY . .

RUN pip install -i https://pypi.douban.com/simple \
    --trusted-host=pypi.douban.com elastic-apm[flask]  flask \
    gunicorn gevent

CMD ["gunicorn", "agent:app", "-c", "gunicorn.conf.py"]