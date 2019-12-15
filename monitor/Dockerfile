FROM openjdk:8

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=monitor.jar

ADD ${JAR_FILE} monitor.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/monitor.jar"]