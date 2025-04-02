# Usa un'immagine base con OpenJDK
#FROM openjdk:22-slim
FROM eclipse-temurin:22 AS build

WORKDIR /app
COPY . /app
RUN apt update && apt install -y maven

# Copia i file del progetto
WORKDIR /app
COPY . .

# Usa OpenJDK 22 per eseguire l'applicazione
FROM eclipse-temurin:22 AS runtime
WORKDIR /app
COPY --from=build /app/target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar /app/target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar

# Espone la porta 42069
EXPOSE 42069

ENTRYPOINT ["java", "-jar", "/app/target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar"]