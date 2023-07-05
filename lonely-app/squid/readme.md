# 生成密码
```
printf "USERNAME:$(openssl passwd -crypt PASSWORD)\n"
```
然后复制到 `lonely-app/squid/squid/htpasswd` 文件中即可，其中 USERNAME 和 PASSWORD 需要你指定你想设置的用户名和密码。