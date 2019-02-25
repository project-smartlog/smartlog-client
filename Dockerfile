FROM openjdk:8-jre-alpine

LABEL maintainer=""
LABEL com.centurylinklabs.watchtower.enable="true"

EXPOSE 8080

WORKDIR /opt/smartlog-client
COPY build/libs/ROOT.war init.sh ./

RUN apk --no-cache add docker

ENTRYPOINT exec /opt/smartlog-client/init.sh

