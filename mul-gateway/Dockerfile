FROM java:8

MAINTAINER <chiangwanyi>

ADD target/mul-gateway-1.2.0.jar app.jar

ADD src/main/resources/application.yml application.yml

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]