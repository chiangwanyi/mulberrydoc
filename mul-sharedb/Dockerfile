FROM node:latest

RUN mkdir /mul-sharedb

WORKDIR /mul-sharedb

ADD . /mul-sharedb

RUN npm install --registry=https://registry.npm.taobao.org

EXPOSE 9003