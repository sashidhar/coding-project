FROM eclipse-temurin:11
RUN apt-get -y update
RUN apt-get -y install git
RUN apt-get -y install maven
RUN git clone https://github.com/sashidhar/coding-project.git
WORKDIR "/coding-project"
RUN mvn clean install
COPY target/calendly-0.0.1-SNAPSHOT.jar calendly-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","calendly-0.0.1-SNAPSHOT.jar"]
