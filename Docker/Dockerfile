FROM openjdk:11-jre-slim
RUN apt-get update && \
    apt-get -y install curl jq
    
WORKDIR /home/ubuntu
COPY ./*.jar ./server.jar

EXPOSE 8080

CMD java -jar server.jar \
    --server.port=8080 \
    --spring.profiles.active=${PROFILE}
