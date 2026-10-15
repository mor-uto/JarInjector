package me.moruto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class GUI extends JFrame {

    private File inputFile, outputFile, fileToInject;

    private JTextField inputPath, outputPath, injectPath;
    private static JTextArea console;

    private JButton injectButton;
    private JLabel status;
    private JProgressBar progress;

    private final JarInjector jarInjector = new JarInjector();

    private final Color bg = new Color(24, 26, 32);
    private final Color panel = new Color(32, 35, 43);
    private final Color fieldBg = new Color(40, 43, 52);
    private final Color blue = new Color(90, 120, 230);
    private final Color text = new Color(230, 233, 240);
    private final Color muted = new Color(150, 155, 170);
    private final Color border = new Color(55, 59, 70);

    public GUI() {
        setTitle("Jar Injector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 620);
        setMinimumSize(new Dimension(680, 520));
        setLocationRelativeTo(null);

        try {
            URL icon = getClass().getResource("/icon.png");
            if (icon != null) {
                setIconImage(new ImageIcon(icon).getImage());
            }
        } catch (Exception ignored) {
        }

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(bg);
        root.setBorder(new EmptyBorder(24, 26, 22, 26));

        JLabel title = new JLabel("Jar Injector");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(text);

        JLabel subtitle = new JLabel(
                "Merge classes and resources into a Java archive"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(muted);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JPanel files = card();
        files.setLayout(new GridLayout(3, 1, 0, 10));

        inputPath = new JTextField();
        injectPath = new JTextField();
        outputPath = new JTextField();

        files.add(fileRow("Input JAR", inputPath, 0, false));
        files.add(fileRow("Inject JAR", injectPath, 2, false));
        files.add(fileRow("Output JAR", outputPath, 1, true));

        JPanel terminal = card();
        terminal.setLayout(new BorderLayout(0, 10));

        JLabel consoleTitle = new JLabel("Console");
        consoleTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        consoleTitle.setForeground(text);

        console = new JTextArea();
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setFont(new Font("Monospaced", Font.PLAIN, 12));
        console.setForeground(new Color(225, 230, 240));
        console.setBackground(new Color(28, 30, 37));
        console.setCaretColor(text);
        console.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(console);
        scroll.setBorder(new LineBorder(border, 1, true));
        scroll.getViewport().setBackground(new Color(28, 30, 37));
        scroll.getVerticalScrollBar().setBackground(panel);
        scroll.getHorizontalScrollBar().setBackground(panel);

        JButton clear = createButton("Clear", fieldBg, text);
        clear.addActionListener(e -> console.setText(""));

        JPanel consoleHeader = new JPanel(new BorderLayout());
        consoleHeader.setOpaque(false);
        consoleHeader.add(consoleTitle, BorderLayout.WEST);
        consoleHeader.add(clear, BorderLayout.EAST);

        terminal.add(consoleHeader, BorderLayout.NORTH);
        terminal.add(scroll, BorderLayout.CENTER);

        content.add(files, BorderLayout.NORTH);
        content.add(terminal, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);

        status = new JLabel("Ready");
        status.setForeground(muted);

        progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setVisible(false);
        progress.setPreferredSize(new Dimension(120, 8));
        progress.setBackground(panel);
        progress.setForeground(blue);
        progress.setBorderPainted(false);

        injectButton = createButton("Inject JAR", blue, Color.WHITE);
        injectButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        injectButton.setPreferredSize(new Dimension(130, 40));
        injectButton.addActionListener(e -> inject());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        left.setOpaque(false);
        left.add(status);
        left.add(progress);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(left, BorderLayout.WEST);
        footer.add(injectButton, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(panel);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        return p;
    }

    private JButton createButton(String label, Color background,
                                 Color foreground) {
        JButton button = new JButton(label);
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel fileRow(String label, JTextField field,
                           int row, boolean save) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JLabel name = new JLabel(label);
        name.setPreferredSize(new Dimension(90, 30));
        name.setForeground(text);
        name.setFont(new Font("SansSerif", Font.BOLD, 12));

        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBackground(fieldBg);
        field.setForeground(text);
        field.setCaretColor(Color.WHITE);
        field.setSelectionColor(blue);
        field.setSelectedTextColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(0, 9, 0, 9)
        ));

        JButton browse = createButton("Browse", fieldBg, text);
        browse.addActionListener(e -> chooseFile(field, row, save));

        p.add(name, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        p.add(browse, BorderLayout.EAST);

        return p;
    }

    private void chooseFile(JTextField field, int row, boolean save) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(save ? "Choose output JAR" : "Choose input JAR");
        chooser.setFileFilter(
                new FileNameExtensionFilter("JAR files (*.jar)", "jar")
        );

        int result = save
                ? chooser.showSaveDialog(this)
                : chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (save && !file.getName().toLowerCase().endsWith(".jar")) {
            file = new File(
                    file.getParentFile(),
                    file.getName() + ".jar"
            );
        }

        field.setText(file.getAbsolutePath());

        if (row == 0) {
            inputFile = file;

            if (outputPath.getText().trim().isEmpty()) {
                String path = file.getAbsolutePath();

                if (path.toLowerCase().endsWith(".jar")) {
                    path = path.substring(0, path.length() - 4);
                }

                outputFile = new File(path + "-injected.jar");
                outputPath.setText(outputFile.getAbsolutePath());
            }
        } else if (row == 1) {
            outputFile = file;
        } else {
            fileToInject = file;
        }
    }

    private void inject() {
        inputFile = read(inputPath);
        fileToInject = read(injectPath);
        outputFile = read(outputPath);

        if (outputFile == null && inputFile != null) {
            String path = inputFile.getAbsolutePath();

            if (path.toLowerCase().endsWith(".jar")) {
                path = path.substring(0, path.length() - 4);
            }

            outputFile = new File(path + "-injected.jar");
            outputPath.setText(outputFile.getAbsolutePath());
        }

        if (!validateFiles()) {
            return;
        }

        final File source = inputFile;
        final File injection = fileToInject;
        final File destination = outputFile;

        injectButton.setEnabled(false);
        progress.setVisible(true);
        status.setText("Injecting...");

        log("Starting injection...");
        log("Input: " + source);
        log("Inject: " + injection);
        log("Output: " + destination);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                JarLoader input = new JarLoader();

                if (!input.loadJar(source)) {
                    throw new RuntimeException(
                            "Failed to load input JAR."
                    );
                }

                log("Loaded input JAR.");

                JarLoader extra = new JarLoader();

                if (!extra.loadJar(injection)) {
                    throw new RuntimeException(
                            "Failed to load injection JAR."
                    );
                }

                log("Loaded injection JAR.");

                jarInjector.inject(input);

                input.getClasses().addAll(extra.getClasses());
                input.getResources().addAll(extra.getResources());

                input.saveJar(destination.getAbsolutePath());

                log("Merged classes and resources.");
                log("JAR written successfully.");

                return null;
            }

            @Override
            protected void done() {
                injectButton.setEnabled(true);
                progress.setVisible(false);

                try {
                    get();

                    status.setText("Injection completed");
                    log("Injection successful!");

                    JOptionPane.showMessageDialog(
                            GUI.this,
                            "Output saved to:\n" + destination,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception e) {
                    Throwable cause = e.getCause() != null
                            ? e.getCause()
                            : e;

                    String message = cause.getMessage();

                    if (message == null || message.trim().isEmpty()) {
                        message = cause.toString();
                    }

                    status.setText("Injection failed");
                    log("ERROR: " + message);

                    JOptionPane.showMessageDialog(
                            GUI.this,
                            message,
                            "Injection failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private File read(JTextField field) {
        String path = field.getText().trim();

        return path.isEmpty() ? null : new File(path);
    }

    private boolean validateFiles() {
        if (inputFile == null || !inputFile.isFile()) {
            return error("Select a valid input JAR.");
        }

        if (fileToInject == null || !fileToInject.isFile()) {
            return error("Select a valid injection JAR.");
        }

        if (inputFile.equals(fileToInject)) {
            return error(
                    "Input and injection JAR must be different files."
            );
        }

        if (outputFile == null || outputFile.isDirectory()) {
            return error("Select a valid output file path.");
        }

        if (outputFile.equals(inputFile)
                || outputFile.equals(fileToInject)) {
            return error(
                    "Output must not overwrite either input JAR."
            );
        }

        File parent = outputFile.getAbsoluteFile().getParentFile();

        if (parent == null) {
            return error("Invalid output directory.");
        }

        if (!parent.exists() && !parent.mkdirs()) {
            return error("Cannot create output directory.");
        }

        return true;
    }

    private boolean error(String message) {
        status.setText("Check configuration");
        log("ERROR: " + message);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid configuration",
                JOptionPane.WARNING_MESSAGE
        );

        return false;
    }

    public static void log(String message) {
        SwingUtilities.invokeLater(() -> {
            if (console != null) {
                console.append(message + "\n");
                console.setCaretPosition(
                        console.getDocument().getLength()
                );
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new GUI().setVisible(true);
        });
    }
}