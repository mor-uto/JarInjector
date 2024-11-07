package me.moruto;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;

public class GUI extends Application {
    private File inputFile, outputFile, fileToInject;
    private TextField inputPathField, outputPathField, injectPathField;
    private static TextArea consoleOutput;
    private final JarInjector jarInjector = new JarInjector();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jar Injector");

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        InputStream iconIS = getClass().getResourceAsStream("/icon.png");
        if (iconIS != null) {
            Image icon = new Image(iconIS);
            primaryStage.getIcons().add(icon);
        }

        BorderPane borderPane = new BorderPane();
        GridPane settingsGrid = new GridPane();
        settingsGrid.setPadding(new Insets(10));
        settingsGrid.setVgap(10);
        settingsGrid.setHgap(10);

        inputPathField = createFileSelector(settingsGrid, "Input Path:", 0, primaryStage, false);
        outputPathField = createFileSelector(settingsGrid, "Output Path:", 1, primaryStage, true);
        injectPathField = createFileSelector(settingsGrid, "File to Inject:", 2, primaryStage, false);

        consoleOutput = new TextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setWrapText(true);
        consoleOutput.setPrefHeight(200);

        Button injectAction = new Button("Inject");
        injectAction.setOnAction(e -> inject());

        BorderPane.setMargin(consoleOutput, new Insets(20));
        borderPane.setTop(settingsGrid);
        borderPane.setCenter(consoleOutput);
        borderPane.setBottom(injectAction);

        Scene scene = new Scene(borderPane, screenBounds.getWidth() / 2, screenBounds.getHeight() / 2);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createFileSelector(GridPane gridPane, String label, int row, Stage primaryStage, boolean save) {
        TextField pathField = new TextField();
        pathField.setPrefWidth(300);
        Button selectButton = new Button("Select File");

        selectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar Files", "*.jar"));
            File selectedFile = save ? fileChooser.showSaveDialog(primaryStage) : fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                pathField.setText(selectedFile.getAbsolutePath());
                if (row == 0) inputFile = selectedFile;
                else if (row == 1) outputFile = selectedFile;
                else fileToInject = selectedFile;
            }
        });

        gridPane.add(new Label(label), 0, row);
        gridPane.add(pathField, 1, row);
        gridPane.add(selectButton, 2, row);
        return pathField;
    }

    private void inject() {
        if (inputPathField.getText().isEmpty() && inputFile == null) {
            log("Please select or enter the input jar path.");
            return;
        }

        if (injectPathField.getText().isEmpty() && fileToInject == null) {
            log("Please select or enter the path of the jar to inject.");
            return;
        }

        if (!inputPathField.getText().isEmpty()) inputFile = new File(inputPathField.getText());
        if (!injectPathField.getText().isEmpty()) fileToInject = new File(injectPathField.getText());

        if (!outputPathField.getText().isEmpty()) {
            outputFile = new File(outputPathField.getText());
        } else if (outputFile == null) {
            outputFile = new File(inputFile.getAbsolutePath().replace(".jar", "-injected.jar"));
            log("No output file selected, defaulting to: " + outputFile.getAbsolutePath());
        }

        JarLoader inputJarLoader = new JarLoader();
        if (!inputJarLoader.loadJar(inputFile)) {
            log("Error loading input jar.");
            return;
        }

        JarLoader injectionJarLoader = new JarLoader();
        if (!injectionJarLoader.loadJar(fileToInject)) {
            log("Error loading injection jar.");
            return;
        }

        jarInjector.inject(inputJarLoader);
        inputJarLoader.getClasses().addAll(injectionJarLoader.getClasses());
        inputJarLoader.getResources().addAll(injectionJarLoader.getResources());
        inputJarLoader.saveJar(outputFile.getAbsolutePath());

        log("Injection successful! Output saved at: " + outputFile.getAbsolutePath());
    }

    public static void log(String message) {
        Platform.runLater(() -> consoleOutput.appendText(message + "\n"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
