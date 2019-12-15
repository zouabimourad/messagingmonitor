FROM openjdk:8

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=agent.jar

ADD ${JAR_FILE} agent.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/agent.jar"]