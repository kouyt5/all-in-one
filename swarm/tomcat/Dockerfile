FROM tomcat:latest

COPY ./aisaps.war /usr/local/tomcat/webapps/

HEALTHCHECK --interval=5s --timeout=5s --retries=3 \
    CMD curl -f localhost:8080/aisaps/|| exit 1