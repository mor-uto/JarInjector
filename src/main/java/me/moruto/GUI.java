package me.moruto;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GUI extends Application {
    private File inputFile;
    private File outputFile;
    private File fileToInject;
    private TextField injectionMainClassInput;
    private static TextArea consoleOutput;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jar Injector");

        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(icon);

        BorderPane borderPane = new BorderPane();

        HBox topButtons = new HBox(10);
        topButtons.setPadding(new Insets(10, 10, 10, 10));
        topButtons.setAlignment(Pos.CENTER);

        Button settingsButton = new Button("Settings");
        Button injectButton = new Button("Inject");

        settingsButton.setPrefWidth(100);
        injectButton.setPrefWidth(100);

        topButtons.getChildren().addAll(settingsButton, injectButton);
        borderPane.setTop(topButtons);

        GridPane settingsGrid = new GridPane();
        settingsGrid.setPadding(new Insets(10, 10, 10, 10));
        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);
        settingsGrid.setAlignment(Pos.CENTER);

        Label inputLabel = new Label("Input Path:");
        GridPane.setConstraints(inputLabel, 0, 0);

        TextField inputPathField = new TextField();
        inputPathField.setPromptText("Enter input path");
        inputPathField.setPrefWidth(300);
        GridPane.setConstraints(inputPathField, 1, 0);

        Button inputPathButton = new Button("Select File");
        GridPane.setConstraints(inputPathButton, 2, 0);

        Label outputLabel = new Label("Output Path:");
        GridPane.setConstraints(outputLabel, 0, 1);

        TextField outputPathField = new TextField();
        outputPathField.setPromptText("Enter output path");
        outputPathField.setPrefWidth(300);
        GridPane.setConstraints(outputPathField, 1, 1);

        Button outputPathButton = new Button("Select File");
        GridPane.setConstraints(outputPathButton, 2, 1);

        Label fileToInjectLabel = new Label("File to Inject:");
        GridPane.setConstraints(fileToInjectLabel, 0, 2);

        TextField fileToInjectField = new TextField();
        fileToInjectField.setPromptText("Enter file to inject");
        fileToInjectField.setPrefWidth(300);
        GridPane.setConstraints(fileToInjectField, 1, 2);

        Button fileToInjectButton = new Button("Select File");
        GridPane.setConstraints(fileToInjectButton, 2, 2);

        Label injectionMainClassLabel = new Label("Injection Main Class:");
        GridPane.setConstraints(injectionMainClassLabel, 0, 3);

        injectionMainClassInput = new TextField();
        injectionMainClassInput.setPromptText("Enter Injection Main Class");
        injectionMainClassInput.setPrefWidth(300);
        GridPane.setConstraints(injectionMainClassInput, 1, 3);

        settingsGrid.getChildren().addAll(
                inputLabel, inputPathField, inputPathButton,
                outputLabel, outputPathField, outputPathButton,
                fileToInjectLabel, fileToInjectField, fileToInjectButton,
                injectionMainClassLabel, injectionMainClassInput
        );

        VBox injectBox = new VBox(10);
        injectBox.setAlignment(Pos.CENTER);
        injectBox.setPadding(new Insets(20, 20, 20, 20));

        consoleOutput = new TextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setWrapText(true);
        consoleOutput.setPrefHeight(200);
        consoleOutput.setPrefWidth(500);

        Button injectAction = new Button("Inject");
        injectAction.setOnAction(e -> inject());
        injectAction.setPrefWidth(100);

        injectBox.getChildren().addAll(consoleOutput, injectAction);

        borderPane.setCenter(settingsGrid);

        settingsButton.setOnAction(e -> borderPane.setCenter(settingsGrid));
        injectButton.setOnAction(e -> borderPane.setCenter(injectBox));

        inputPathButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar Files", "*.jar"));
            inputFile = fileChooser.showOpenDialog(primaryStage);
            if (inputFile != null) {
                inputPathField.setText(inputFile.getAbsolutePath());
            }
        });

        outputPathButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar Files", "*.jar"));
            outputFile = fileChooser.showSaveDialog(primaryStage);
            if (outputFile != null) {
                outputPathField.setText(outputFile.getAbsolutePath());
            }
        });

        fileToInjectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar Files", "*.jar"));
            fileToInject = fileChooser.showOpenDialog(primaryStage);
            if (fileToInject != null) {
                fileToInjectField.setText(fileToInject.getAbsolutePath());
            }
        });

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void inject() {
        if (inputFile == null) {
            log("Please select the input file.");
            return;
        }

        if (outputFile == null) {
            log("Output file is invalid, defaulting to \"input path + \"-injected\"\"");
            outputFile = new File(inputFile.getAbsolutePath().replace(".jar", "-injected.jar"));
        }

        if (fileToInject == null) {
            log("Please select the file to inject.");
            return;
        }

        String output = outputFile.getAbsolutePath();
        String injectionMainClass = injectionMainClassInput.getText();
        consoleOutput.clear();

        if (!inputFile.exists()) {
            log("Input file is missing!");
            return;
        }

        JarLoader inputJarLoader = new JarLoader();
        boolean mainLoaded = inputJarLoader.loadJar(inputFile);
        if (!mainLoaded) {
            log("Error loading the main jar. Please try again!");
            return;
        } else log("Main Jar successfully loaded!");

        JarLoader injectionJarLoader = new JarLoader();
        boolean loaded = injectionJarLoader.loadJar(fileToInject);
        if (!loaded) {
            log("Error loading the Injection jar. Please try again!");
            return;
        } else log("Injection Jar successfully loaded!");

        JarInjector.inject(injectionMainClass.replace(".", "/"), inputJarLoader.classes);
        inputJarLoader.classes.addAll(injectionJarLoader.classes);
        inputJarLoader.resources.addAll(injectionJarLoader.resources);

        inputJarLoader.saveJar(output);
        log("Successfully saved the jar!");
    }

    public static void log(String message) {
        Platform.runLater(() -> consoleOutput.appendText(message + "\n"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
