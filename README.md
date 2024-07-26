# JarInjector

JarInjector is a simple ASM injection tool that allows you to inject classes and modify JAR files.

## Usage

### Running the Application

To run JarInjector, you have two options:
1. **Double-click the JAR File**: Simply double-click the `JarInjector.jar` file.
2. **Command Line**: Use the following command to run the application from the command line:
```bash
java -jar JarInjector.jar
```
![Preview](https://github.com/Mor-uto/JarInjector/raw/main/github/image1.jpg)

### Configuration
Input path: path to the jar you want to be injected

Output path: path to the output jar

File to inject: path to the jar that will be injected into the input jar

Main class: main class of input jar. example -> me.moruto.classes.Main

Main Method: Main running method in the Main class. example -> main (refers to `public static void main(String[] args)` or anything method named main for example)
### Injecting
Once you finish configuring it go to the inject tab and press inject
