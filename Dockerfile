FROM gradle:6.7 as builder

COPY . .
COPY src ./src

RUN gradle shadowJar --no-daemon

FROM openjdk:8-jre-alpine

COPY --from=builder /home/gradle/build/libs/crawler.jar /crawler.jar

ENTRYPOINT ["java", "-jar"]

CMD ["-Djava.security.egd=file:/dev/./urandom", "/crawler.jar"]