package me.moruto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GUI extends JFrame {

    private File inputFile;
    private File outputFile;
    private File fileToInject;

    private JTextField inputPathField;
    private JTextField outputPathField;
    private JTextField injectPathField;

    private static JTextArea consoleOutput;
    private JButton injectButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private final JarInjector jarInjector = new JarInjector();

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color PANEL = Color.WHITE;
    private static final Color PRIMARY = new Color(70, 100, 220);
    private static final Color PRIMARY_HOVER = new Color(55, 82, 195);
    private static final Color TEXT = new Color(35, 38, 45);
    private static final Color MUTED = new Color(110, 115, 125);
    private static final Color BORDER = new Color(220, 223, 230);

    public GUI() {
        setTitle("Jar Injector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 500));
        setSize(850, 600);
        setLocationRelativeTo(null);

        loadIcon();
        setupUI();
    }

    private void loadIcon() {
        try {
            InputStream input = getClass().getResourceAsStream("/icon.png");

            if (input != null) {
                ImageIcon icon = new ImageIcon(
                        new ImageIcon(input.readAllBytes())
                                .getImage()
                );

                setIconImage(icon.getImage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 0, 15, 0));

        center.add(createFilePanel(), BorderLayout.NORTH);
        center.add(createConsolePanel(), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Jar Injector");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Inject classes and resources into a Java archive");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(subtitle);

        panel.add(text, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFilePanel() {
        JPanel panel = createCard();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        inputPathField = new JTextField();
        outputPathField = new JTextField();
        injectPathField = new JTextField();

        addFileRow(panel, gbc, 0, "Input JAR", inputPathField, false);
        addFileRow(panel, gbc, 1, "Output JAR", outputPathField, true);
        addFileRow(panel, gbc, 2, "Inject JAR", injectPathField, false);

        return panel;
    }

    private void addFileRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field,
            boolean save
    ) {
        JLabel name = new JLabel(label);
        name.setFont(new Font("SansSerif", Font.BOLD, 13));
        name.setForeground(TEXT);

        field.setPreferredSize(new Dimension(400, 36));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));

        JButton browse = createSecondaryButton("Browse");

        browse.addActionListener(e -> chooseFile(field, row, save));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(name, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(browse, gbc);
    }

    private JPanel createConsolePanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Console");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setLineWrap(true);
        consoleOutput.setWrapStyleWord(true);
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        consoleOutput.setForeground(new Color(220, 225, 235));
        consoleOutput.setBackground(new Color(30, 32, 38));
        consoleOutput.setCaretColor(Color.WHITE);
        consoleOutput.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.setBorder(new LineBorder(new Color(45, 48, 55), 1, true));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(150, 8));

        injectButton = createPrimaryButton("Inject JAR");
        injectButton.setPreferredSize(new Dimension(130, 40));
        injectButton.addActionListener(e -> inject());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(statusLabel);
        left.add(Box.createHorizontalStrut(15));
        left.add(progressBar);

        panel.add(left, BorderLayout.WEST);
        panel.add(injectButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY);
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(TEXT);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(BORDER, 1, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void chooseFile(
            JTextField field,
            int row,
            boolean save
    ) {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(save ? "Select output JAR" : "Select JAR file");

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Java Archive (*.jar)",
                        "jar"
                )
        );

        int result = save
                ? chooser.showSaveDialog(this)
                : chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selected = chooser.getSelectedFile();

        if (save && !selected.getName().toLowerCase().endsWith(".jar")) {
            selected = new File(
                    selected.getParentFile(),
                    selected.getName() + ".jar"
            );
        }

        field.setText(selected.getAbsolutePath());

        if (row == 0) {
            inputFile = selected;
        } else if (row == 1) {
            outputFile = selected;
        } else {
            fileToInject = selected;
        }
    }

    private void inject() {
        resolveFiles();

        if (!validateFiles()) {
            return;
        }

        injectButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Injecting...");

        log("Starting injection...");
        log("Input: " + inputFile.getAbsolutePath());
        log("Inject: " + fileToInject.getAbsolutePath());
        log("Output: " + outputFile.getAbsolutePath());

        new Thread(() -> {
            try {
                performInjection();

                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    injectButton.setEnabled(true);
                    statusLabel.setText("Injection completed");

                    log("");
                    log("Injection successful!");
                    log("Output saved at:");
                    log(outputFile.getAbsolutePath());
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    injectButton.setEnabled(true);
                    statusLabel.setText("Injection failed");

                    log("");
                    log("ERROR: " + ex.getMessage());
                });
            }
        }, "JarInjector-Worker").start();
    }

    private void performInjection() {
        JarLoader inputJarLoader = new JarLoader();

        if (!inputJarLoader.loadJar(inputFile)) {
            throw new RuntimeException("Failed to load input JAR.");
        }

        log("Loaded input JAR.");

        JarLoader injectionJarLoader = new JarLoader();

        if (!injectionJarLoader.loadJar(fileToInject)) {
            throw new RuntimeException("Failed to load injection JAR.");
        }

        log("Loaded injection JAR.");

        jarInjector.inject(inputJarLoader);

        log("Injector processing completed.");

        inputJarLoader.getClasses()
                .addAll(injectionJarLoader.getClasses());

        inputJarLoader.getResources()
                .addAll(injectionJarLoader.getResources());

        log("Merged classes and resources.");

        inputJarLoader.saveJar(outputFile.getAbsolutePath());

        log("JAR written successfully.");
    }

    private void resolveFiles() {
        if (!inputPathField.getText().trim().isEmpty()) {
            inputFile = new File(inputPathField.getText().trim());
        }

        if (!injectPathField.getText().trim().isEmpty()) {
            fileToInject = new File(injectPathField.getText().trim());
        }

        if (!outputPathField.getText().trim().isEmpty()) {
            outputFile = new File(outputPathField.getText().trim());
        }

        if (outputFile == null && inputFile != null) {
            String path = inputFile.getAbsolutePath();

            if (path.toLowerCase().endsWith(".jar")) {
                path = path.substring(0, path.length() - 4);
            }

            outputFile = new File(path + "-injected.jar");

            outputPathField.setText(outputFile.getAbsolutePath());

            log("No output selected.");
            log("Using default: " + outputFile.getAbsolutePath());
        }
    }

    private boolean validateFiles() {
        if (inputFile == null) {
            log("ERROR: Please select an input JAR.");
            return false;
        }

        if (!inputFile.isFile()) {
            log("ERROR: Input JAR does not exist.");
            return false;
        }

        if (fileToInject == null) {
            log("ERROR: Please select a JAR to inject.");
            return false;
        }

        if (!fileToInject.isFile()) {
            log("ERROR: Injection JAR does not exist.");
            return false;
        }

        if (inputFile.equals(fileToInject)) {
            log("ERROR: Input and injection JAR cannot be the same file.");
            return false;
        }

        if (outputFile == null) {
            log("ERROR: No output file specified.");
            return false;
        }

        if (outputFile.equals(inputFile)) {
            log("ERROR: Output cannot overwrite the input JAR.");
            return false;
        }

        return true;
    }

    public static void log(String message) {
        SwingUtilities.invokeLater(() -> {
            consoleOutput.append(message + "\n");
            consoleOutput.setCaretPosition(
                    consoleOutput.getDocument().getLength()
            );
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