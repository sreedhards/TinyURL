FROM java:8
VOLUME /tmp
ADD build/libs/tinyurl-0.0.1-SNAPSHOT.jar tinyurl-spring-redis.jar
RUN bash -c 'touch /tinyurl-spring-redis.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tinyurl-spring-redis.jar"]
