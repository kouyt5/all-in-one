# jupyter

jupyter notebook 是一个在线运行 python 代码的服务器，实际上可以使用 code-server 替代。但相对来说比较轻量。jupyter lab 是 notebook 的升级版本，感觉界面有点儿像 vscode，于是后面改成部署 lab 了，只需要把 CMD 命令中`jupyter notebook` 变成 `start.sh jupyter lab` 即可。
jupyter 特性：https://zhuanlan.zhihu.com/p/67959768

参考链接：

*   https://www.kubeflow.org/docs/notebooks/custom-notebook/

## 密码配置

第一种简单但不自动化的方式。

```bash
CMD ["sh","-c", "jupyter notebook --ip=0.0.0.0 --no-browser --allow-root --port=8888 \
--NotebookApp.allow_origin='*' \
#  --NotebookApp.token='' \ # 有该属性无法设置密码
--notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"]
```

启动后输入控制台输出的 token 字段设置密码。但这种方式总需要人手动查看 token 然后复制，比较麻烦，我就在想有没有可以把密码放入配置文件的方式，每次重新构建镜像的时候不需要人为干预，使用预先设定的密码就可以了。虽然感觉这个想法应该 jupyter 的作者能够想到，但是按照常理来说，肯定没那么简单。经过摸索证明是这样的。因为考虑到安全性，似乎作者不允许直接将密码储存到文本中，那咋办，不可能每次都只能输入 token 吧，其实作者的思路是先将密码进行加密，加密后储存加密的密码就可以了，登录的时候输入明文密码，传输加密后的密码与这个密码作对比，这样就提高了安全性。其实我觉得为什么要把配置弄得这么复杂呢，加密的过程完全可以由程序自动处理呀。但自己不可能改底层代码，先将就着用把。思路如下：

*   首先随便进入一个juypter 容器（随便你，本地也可以），输入 `jupyter notebook password`
*   输入明文密码获取到 sha 算法加密的密码（注意这个与网上生成的不一样）

```bash
根据提示打开生成的密码存放文件
$ cat /home/jovyan/.jupyter/jupyter_notebook_config.json
{
  "NotebookApp": {
    "password": "sha1:8afe621e8f22:51015a88aadb5a9802be3aaa8425e797fccb8666"
  }
```

*   获取到加密密码后再启动 jupyter 时候设置密码

```bash
CMD ["sh","-c", "jupyter notebook --ip=0.0.0.0 --no-browser --allow-root --port=8888 \
 --NotebookApp.password='sha1:8afe621e8f22:51015a88aadb5a9802be3aaa8425e797fccb8666' \
  --NotebookApp.allow_origin='*' \
 --notebook-dir=/workdir --NotebookApp.base_url=${NB_PREFIX}"]
```

然后输入明文密码就可以登录了。

![继承关系](http://interactive.blockdiag.com/image?compression=deflate\&encoding=base64\&src=eJyFzTEPgjAQhuHdX9Gws5sQjGzujsaYKxzmQrlr2msMGv-71K0srO_3XGud9NNA8DSfgzESCFlBSdi0xkvQAKTNugw4QnL6GIU10hvX-Zh7Z24OLLq2SjaxpvP10lX35vCf6pOxELFmUbQiUz4oQhYzMc3gCrRt2cWe_FKosmSjyFHC6OS1AwdQWCtyj7sfh523_BI9hKlQ25YdOFdv5fcH0kiEMA)
