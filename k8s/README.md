# k8s learn

## refer 
+ https://github.com/AliyunContainerService/minikube/wiki 阿里镜像配置
## 启动dashboard
'''
kubectl proxy --address='0.0.0.0' --port=8001 --accept-hosts='^*$'
minikube dashboard
'''