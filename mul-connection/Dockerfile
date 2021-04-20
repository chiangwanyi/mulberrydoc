FROM node:latest

RUN mkdir /mul-connection

WORKDIR /mul-connection

ADD . /mul-connection

RUN npm install -g typescript --registry=https://registry.npm.taobao.org

RUN npm install --registry=https://registry.npm.taobao.org

RUN tsc

EXPOSE 9100