FROM bellsoft/liberica-openjdk-alpine:17.0.8

ARG UID=1001
RUN adduser -S gretlx -u $UID

ENV HOME=/app
WORKDIR $HOME

COPY target/planregister-*.jar ./application.jar

RUN chown $UID:0 . && \
    chmod 0775 . && \
    ls -la

USER $UID
EXPOSE 8080
ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true
CMD java -XX:MaxRAMPercentage=80.0 -jar application.jar 