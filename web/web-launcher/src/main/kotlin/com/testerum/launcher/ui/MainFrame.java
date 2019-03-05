package com.testerum.launcher.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.testerum.launcher.config.ConfigManager;
import com.testerum.launcher.config.model.Config;
import com.testerum.launcher.runner.TesterumExecuter;
import kotlin.Unit;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.net.URI;

public class MainFrame extends JFrame {

    private final ConfigManager configManager;
    private final TesterumExecuter testerumExecuter;

    private Config config;

    private JPanel rootPanel;
    private JTextField urlTextFiled;
    private JButton openInBrowserButton;
    private JButton copyUrlToClipboardButton;
    private JTextField portInputField;
    private JButton saveAndRestartApplicationButton;
    private JLabel statusLabel;
    private JLabel arrowImage;
    private JLabel portErrorMessage;

    public MainFrame(@NotNull final ConfigManager configManager,
                     @NotNull final TesterumExecuter testerumExecuter) throws HeadlessException {
        this.configManager = configManager;
        this.testerumExecuter = testerumExecuter;

        this.testerumExecuter.setServerStartedHandler(() -> {
            setServerAsStarted();
            return Unit.INSTANCE;
        });
        this.testerumExecuter.setServerPortNotAvailableHandler(() -> {
            setServerPortNotAvailable();
            return Unit.INSTANCE;
        });

        setTitle("Testerum Launcher");
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(
                new ImageIcon(MainFrame.class.getResource("/favicon.png")).getImage()
        );
        setResizable(false);
        saveAndRestartApplicationButton.setEnabled(false);
        arrowImage.setVisible(false);
        portErrorMessage.setVisible(false);

        setSize(800, 400);

        config = configManager.getConfig();
        setValuesInFields(config);

        copyUrlToClipboardButton.addActionListener(this::onCopyUrlToClipboardClicked);
        openInBrowserButton.addActionListener(this::onOpenInBrowserClicked);

        portInputField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(final DocumentEvent e) {
                onPortChanged();
            }

            public void removeUpdate(final DocumentEvent e) {
                onPortChanged();
            }

            public void insertUpdate(final DocumentEvent e) {
                onPortChanged();
            }
        });

        saveAndRestartApplicationButton.addActionListener(this::onSaveAndRestartApplicationClicked);
    }

    private void setServerAsStarted() {
        arrowImage.setVisible(true);
        statusLabel.setIcon(null);
        statusLabel.setText("Testerum Server Started");
        portErrorMessage.setVisible(false);
        openInBrowserButton.setEnabled(true);
        saveAndRestartApplicationButton.setEnabled(true);
    }

    private void setServerAsLoading() {
        arrowImage.setVisible(false);
        statusLabel.setIcon(new ImageIcon(getClass().getResource("/loading.gif")));
        statusLabel.setText(" Restarting Testerum Server");
        portErrorMessage.setVisible(false);
        openInBrowserButton.setEnabled(false);
    }

    private void setServerPortNotAvailable() {
        arrowImage.setVisible(false);
        statusLabel.setIcon(new ImageIcon(getClass().getResource("/attention.gif")));
        statusLabel.setText(" Selected port is not available!");
        portErrorMessage.setVisible(true);
        openInBrowserButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private void onCopyUrlToClipboardClicked(final ActionEvent actionEvent) {
        final StringSelection stringSelection = new StringSelection(urlTextFiled.getText());
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @SuppressWarnings("unused")
    private void onOpenInBrowserClicked(final ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI(urlTextFiled.getText()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void onPortChanged() {
        final int port = config.getPort();
        final String newPortAsString = portInputField.getText();

        if (!StringUtils.isNumeric(newPortAsString)) {
            saveAndRestartApplicationButton.setEnabled(false);
            return;
        }

        final int newPort = Integer.parseInt(newPortAsString);
        if (newPort < 1024 || newPort > 65535 || port == newPort) {
            saveAndRestartApplicationButton.setEnabled(false);
            return;
        }
        saveAndRestartApplicationButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private void onSaveAndRestartApplicationClicked(final ActionEvent actionEvent) {
        // save & reload config
        final Config newConfig = new Config(Integer.parseInt(portInputField.getText()));
        configManager.saveConfig(newConfig);

        reloadConfig();
        setServerAsLoading();

        // restart Testerum
        testerumExecuter.stopTesterum();
        testerumExecuter.startTesterum();
    }

    private void reloadConfig() {
        config = configManager.getConfig();
        setValuesInFields(config);
    }

    private void setValuesInFields(final Config config) {
        portInputField.setText("" + config.getPort());
        urlTextFiled.setText("http://localhost:" + config.getPort() + "/");
    }


    public void centerOnScreen() {
        setLocationRelativeTo(null);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new javax.swing.JPanel();
        rootPanel.setLayout(new FormLayout("fill:d:noGrow,fill:d:grow", "center:60dlu:noGrow,top:4dlu:grow(1.1),center:d:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,fill:d:grow(3.9),bottom:d:noGrow,top:10dlu:noGrow,center:max(d;4px):noGrow"));
        final javax.swing.JPanel panel1 = new javax.swing.JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        CellConstraints cc = new CellConstraints();
        rootPanel.add(panel1, cc.xyw(1, 1, 2, CellConstraints.DEFAULT, CellConstraints.FILL));
        final javax.swing.JLabel label1 = new javax.swing.JLabel();
        panel1.add(label1, cc.xy(1, 1));
        final javax.swing.JPanel panel2 = new javax.swing.JPanel();
        panel2.setLayout(new FormLayout("fill:max(d;10dlu):noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:202px:grow,left:4dlu:noGrow,fill:90dlu:noGrow,right:10dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:50px:noGrow"));
        rootPanel.add(panel2, cc.xyw(1, 3, 2, CellConstraints.DEFAULT, CellConstraints.CENTER));
        copyUrlToClipboardButton = new javax.swing.JButton();
        panel2.add(copyUrlToClipboardButton, cc.xy(7, 1));
        openInBrowserButton = new javax.swing.JButton();
        panel2.add(openInBrowserButton, cc.xy(5, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));
        final javax.swing.JLabel label2 = new javax.swing.JLabel();
        panel2.add(label2, cc.xy(3, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
        urlTextFiled = new javax.swing.JTextField();
        panel2.add(urlTextFiled, cc.xy(5, 1, CellConstraints.FILL, CellConstraints.CENTER));
        arrowImage = new javax.swing.JLabel();
        panel2.add(arrowImage, cc.xy(5, 5, CellConstraints.CENTER, CellConstraints.DEFAULT));
        final javax.swing.JPanel panel3 = new javax.swing.JPanel();
        panel3.setLayout(new FormLayout("fill:max(d;10dlu):noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,center:d:noGrow,left:4dlu:noGrow,left:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:m:noGrow,right:d:noGrow,left:10dlu:noGrow,fill:max(d;4px):noGrow", "center:30px:grow"));
        rootPanel.add(panel3, cc.xy(2, 7, CellConstraints.FILL, CellConstraints.BOTTOM));
        final javax.swing.JLabel label3 = new javax.swing.JLabel();
        panel3.add(label3, cc.xy(3, 1));
        saveAndRestartApplicationButton = new javax.swing.JButton();
        panel3.add(saveAndRestartApplicationButton, cc.xy(11, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        portInputField = new javax.swing.JTextField();
        panel3.add(portInputField, cc.xy(5, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        portErrorMessage = new javax.swing.JLabel();
        panel3.add(portErrorMessage, cc.xy(7, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        statusLabel = new javax.swing.JLabel();
        rootPanel.add(statusLabel, cc.xy(2, 6, CellConstraints.CENTER, CellConstraints.TOP));
    }

    /**
     * @noinspection ALL
     */
    public javax.swing.JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
