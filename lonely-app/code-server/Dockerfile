FROM codercom/code-server:latest
USER root
WORKDIR /tmp
COPY ./apt-debain10.list /etc/apt/sources.list
# python安装
RUN apt-get update && apt-get install python3-dev python3-pip -y
# java 安装
RUN apt-get install openjdk-11-source openjdk-11-jdk -y
# maven 安装
RUN apt-get install wget -y \
    && wget -c https://apache.website-solution.net/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz \
    && tar -xvf apache-maven-3.6.3-bin.tar.gz \
    && rm -f apache-maven-3.6.3-bin.tar.gz \
    && mv  apache-maven-3.6.3/ /usr/local/
RUN echo "MAVEN_HOME=/usr/local/apache-maven-3.6.3/ \n\
export MAVEN_HOME \n\
export PATH=\${PATH}:\${MAVEN_HOME}/bin" >> /etc/profile \
    && /bin/bash -c "source /etc/profile"
WORKDIR /home/coder/project
EXPOSE 8080

