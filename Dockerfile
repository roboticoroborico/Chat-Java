# Usa un'immagine base con OpenJDK 22
FROM eclipse-temurin:22

# Installa Maven
RUN apt-get update && apt-get install -y maven wget unzip

# Imposta la directory di lavoro
WORKDIR /app

# Scarica JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/22/openjfx-22_linux-x64_bin-sdk.zip && \
    unzip openjfx-22_linux-x64_bin-sdk.zip && \
    mv javafx-sdk-22 /opt/javafx

# Copia i file del progetto nel container
COPY . .

# Imposta il module-path per JavaFX
ENV MODULE_PATH="/opt/javafx/lib"

# Compila il progetto con Maven
RUN mvn clean package -DskipTests

# Esegui l'applicazione con i moduli JavaFX
CMD ["java", "--module-path", "/opt/javafx/lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics", "-jar", "target/demo.jar"]
