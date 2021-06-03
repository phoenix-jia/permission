FROM registry.cn-shanghai.aliyuncs.com/fame/maven:3-jdk-8
WORKDIR /usr/src/app
RUN echo "Asia/Shanghai" > /etc/timezone && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

COPY . /usr/src/app
RUN mvn package

ENV PORT 80
EXPOSE $PORT
CMD [ "sh", "-c", "mvn -Dserver.port=${PORT} -Dspring-boot.run.jvmArguments='-Xms2048m -Xmx2048m' spring-boot:run" ]
