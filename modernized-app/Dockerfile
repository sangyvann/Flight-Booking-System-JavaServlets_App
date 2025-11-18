FROM openjdk:17-jdk-slim

LABEL maintainer="Turkish Airlines Development Team"
LABEL version="2.0.0"
LABEL description="Turkish Airlines Flight Booking System - Modernized"

# Set working directory
WORKDIR /app

# Create app user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy Maven files for dependency caching
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Create final image
FROM openjdk:17-jre-slim

WORKDIR /app

# Create app user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Copy the built jar
COPY --from=0 /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown -R spring:spring /app
USER spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]