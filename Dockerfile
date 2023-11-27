FROM amazoncorretto:11.0.20-alpine3.17

# Set the working directory inside the container
WORKDIR /app

#COPY scala-library-2.13.11.jar /app

# Install sbt (if not already installed)
RUN apk update && apk add bash
RUN wget https://github.com/sbt/sbt/releases/download/v1.9.3/sbt-1.9.3.tgz
RUN tar -xvzf sbt-1.9.3.tgz

# Copy the JAR file into the container
COPY toDo-list-assembly-1.0-SNAPSHOT.jar /app

# Expose the port your application listens on (if needed)
EXPOSE 9000

# Define the command to run your application
#ENTRYPOINT ["java", "-cp", "toDo-list-assembly-1.0-SNAPSHOT.jar", "Main"]

CMD ["java", "-jar", "/app/toDo-list-assembly-1.0-SNAPSHOT.jar"]
