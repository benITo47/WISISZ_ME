# Use JDK 17
FROM eclipse-temurin:21-jdk

# Install tools for network testing & bash
#RUN apt-get update && apt-get install -y bash curl netcat
 

# Set work directory
WORKDIR /app

# Copy everything (including mvnw, pom.xml, src/)
COPY . .

# Make sure mvnw is executable
RUN chmod +x ./mvnw

# Expose default Spring Boot port
EXPOSE 8080

# Entry point – uses mvnw (development style)
CMD ["./mvnw", "spring-boot:run"]

