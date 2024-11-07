# JarInjector

JarInjector - simple and small injection tool that allows you to inject classes and modify Jar files.

## Usage

### Running the Application

To run JarInjector, you have two options:
1. **Double-click the JAR File**: Simply double-click the `JarInjector.jar` file.
2. **Command Line**: Use the following command to run the application from the command line:
```bash
java -jar JarInjector.jar
```
![image](https://github.com/user-attachments/assets/606c2948-5233-4706-8131-529302650d83)

### Configuration
Input path: path to the jar you want to be injected

Output path: path to the output jar

File to inject: path to the jar that will be injected into the input jar

Version Safety: ensure the the file to inject's java version isnt higher than the input's version
### Injecting
Once you finish configuring it go to the inject tab and press inject

### Libs
JavaFX (org.openjfx.javafx) - GUI

Asm (org.ow2.asm) - Bytecode Manipulation
