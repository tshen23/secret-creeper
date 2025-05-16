# MyMinecraftPlugin

## Prerequisites

* **Java Development Kit (JDK) 17**
* **IntelliJ IDEA** (Community or Ultimate)
* **Build Tool**: Maven 3.6+ **or** Gradle 6+
* **Paper API** (matching your server version, e.g., 1.21.4)
* **Local Minecraft server** (Paper)

## Setup Environment

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/MyMinecraftPlugin.git
   cd MyMinecraftPlugin
   ```

2. **Import into IntelliJ**:

   * Open IntelliJ IDEA.
   * Go to **File > Open...**, select the project folder.
   * If prompted, import as a Maven or Gradle project.
   * Set the **Project SDK** to JDK 17 (File > Project Structure > SDKs).

3. **Verify Paper Dependency**:

   * **Maven**: In `pom.xml`, ensure you have:

     ```xml
     <dependencies>
       <dependency>
         <groupId>io.papermc.paper</groupId>
         <artifactId>paper-api</artifactId>
         <version>1.21.4-R0.1-SNAPSHOT</version>
         <scope>provided</scope>
       </dependency>
     </dependencies>
     ```

   * **Gradle**: In `build.gradle`, verify:

     ```groovy
     dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
     }
     ```

## Build the Project

* **Maven**:

  ```bash
  mvn clean package
  ```

  * Outputs `MyMinecraftPlugin.jar` in the `target/` directory.

* **Gradle**:

  ```bash
  ./gradlew clean build
  ```

  * Outputs `SecretCreeper-1.0-SNAPSHOT.jar` in `build/libs/`.

## Running and Testing

1. **Install Plugin**:

   * Copy the built JAR file (`SecretCreeper-1.0-SNAPSHOT.jar`) into your Paper server's `plugins/` folder.

2. **Start the Server**:

   ```bash
   java -Xms1G -Xmx1G -jar paper-1.21.4.jar
   ```

3. **Join and Test**:

   * Launch Minecraft and connect to `localhost:25565`.
   * **Commands**: Type `/` + whatever the command name is in chat to test plugin commands.
   * Watch the server console for log messages indicating plugin load status and any events.


## Troubleshooting

* **Plugin not loading?** Check the server console for stack traces; confirm JDK 17 is used and Paper API version matches your server.
* **Build errors?** Ensure your IntelliJ project SDK and language level are set to Java 17 and that Maven/Gradle downloads dependencies.
* **Feature not working?** Double-check commands and key bindings as defined in your plugin code (`plugin.yml` and event handlers).

---
