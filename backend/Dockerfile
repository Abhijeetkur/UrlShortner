# ============================================
# Stage 1: Build the application
# ============================================
FROM eclipse-temurin:22-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./mvnw package -DskipTests -B

# ============================================
# Stage 2: Run the application
# ============================================
FROM eclipse-temurin:22-jre

WORKDIR /app

# Install openssl for generating keystores from PEM files
RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Create the entrypoint script that generates keystores from Render Secret Files
RUN cat <<'SCRIPT' > /app/entrypoint.sh
#!/bin/bash
set -e

CERTS_DIR=/app/certs
mkdir -p "$CERTS_DIR"

# Check if Render Secret Files exist at /etc/secrets/
if [ -f /etc/secrets/ca.pem ] && [ -f /etc/secrets/service.cert ] && [ -f /etc/secrets/service.key ]; then
  echo ">>> Generating Kafka SSL truststore and keystore from secret files..."

  # 1. Create truststore (JKS) from CA certificate
  keytool -import -file /etc/secrets/ca.pem \
    -alias aiven-ca \
    -keystore "$CERTS_DIR/client.truststore.jks" \
    -storepass changeit \
    -noprompt

  # 2. Create keystore (PKCS12) from client cert + key
  openssl pkcs12 -export \
    -in /etc/secrets/service.cert \
    -inkey /etc/secrets/service.key \
    -out "$CERTS_DIR/client.keystore.p12" \
    -name aiven-client \
    -passout pass:changeit

  echo ">>> Kafka SSL keystores generated successfully at $CERTS_DIR"
else
  echo ">>> WARNING: Kafka SSL secret files not found at /etc/secrets/. Skipping keystore generation."
fi

# Start the Spring Boot application
exec java -jar /app/app.jar --server.port=${PORT:-8080}
SCRIPT
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
