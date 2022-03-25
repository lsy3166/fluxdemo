# Start with a base image containing Java runtime
FROM openjdk:11-jre-slim

# Add Author info
LABEL maintainer="lsy3166@gmail.com"

# Add a volume to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 4001

# The application's jar file
ARG JAR_FILE_NAME=fluxdemo-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=build/libs/${JAR_FILE_NAME}

# Add the application's jar to the container
ADD ${JAR_FILE} ${JAR_FILE_NAME}

# Run the jar file
# ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/fluxdemo-0.0.1-SNAPSHOT.jar"]
ENV E_JAR_FILE_NAME=/${JAR_FILE_NAME}
ENTRYPOINT java -Dspring.profiles.active=dev -jar ${E_JAR_FILE_NAME}


# 스프링 부트 gradle build
# docker build 후 아래 실행 
# gradlew build && java -jar build/libs/fluxdemo-docker-0.1.0.jar
# docker build -t fluxdemo/fluxdemo-docker .
# docker run -it -p 4001:4001 --name fluxdemo fluxdemo/fluxdemo-docker
