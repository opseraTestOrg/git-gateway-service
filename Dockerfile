FROM gradle:6.9-jdk11 AS build
ENV DOCKER_ENV=dev
ARG jfrog_password_arg
ENV jfrog_password=$jfrog_password_arg
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon

FROM eclipse-temurin:11-jre
RUN apt-get update && apt-get install -y curl dnsutils iputils-ping
RUN mkdir -p /apps/OpsERA/components/git-gateway-service
COPY --from=build /home/gradle/src/build/libs/*.jar /apps/OpsERA/components/git-gateway-service/git-gateway-service.jar
ENV TINI_VERSION v0.19.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
EXPOSE 8094
ENTRYPOINT exec /tini -- java -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0 -Dspring.profiles.active=$DOCKER_ENV $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /apps/OpsERA/components/git-gateway-service/git-gateway-service.jar
