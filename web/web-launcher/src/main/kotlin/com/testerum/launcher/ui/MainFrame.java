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
        onOpenTesterumInBrowser();
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
        openInBrowserButton.setEnabled(false);
        saveAndRestartApplicationButton.setEnabled(true);
    }

    @SuppressWarnings("unused")
    private void onCopyUrlToClipboardClicked(final ActionEvent actionEvent) {
        final StringSelection stringSelection = new StringSelection(urlTextFiled.getText());
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @SuppressWarnings("unused")
    private void onOpenInBrowserClicked(final ActionEvent actionEvent) {
        onOpenTesterumInBrowser();
    }

    private void onOpenTesterumInBrowser() {
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
        rootPanel = new JPanel();
        rootPanel.setLayout(new FormLayout("fill:d:noGrow,fill:d:grow", "center:60dlu:noGrow,top:4dlu:grow(1.1),center:d:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,fill:d:grow(3.9),bottom:d:noGrow,top:10dlu:noGrow,center:max(d;4px):noGrow"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        CellConstraints cc = new CellConstraints();
        rootPanel.add(panel1, cc.xyw(1, 1, 2, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(0);
        label1.setIcon(new ImageIcon(getClass().getResource("/logo.png")));
        label1.setText("");
        panel1.add(label1, cc.xy(1, 1));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:max(d;10dlu):noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:202px:grow,left:4dlu:noGrow,fill:90dlu:noGrow,right:10dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:50px:noGrow"));
        panel2.setMaximumSize(new Dimension(509, 104));
        rootPanel.add(panel2, cc.xyw(1, 3, 2, CellConstraints.DEFAULT, CellConstraints.CENTER));
        copyUrlToClipboardButton = new JButton();
        copyUrlToClipboardButton.setEnabled(true);
        copyUrlToClipboardButton.setHorizontalTextPosition(0);
        copyUrlToClipboardButton.setText("Copy URL to clipboard");
        panel2.add(copyUrlToClipboardButton, cc.xy(7, 1));
        openInBrowserButton = new JButton();
        openInBrowserButton.setEnabled(false);
        openInBrowserButton.setText("Open in browser");
        panel2.add(openInBrowserButton, cc.xy(5, 3, CellConstraints.CENTER, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(2);
        label2.setHorizontalTextPosition(2);
        label2.setText("Application URL:");
        panel2.add(label2, cc.xy(3, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
        urlTextFiled = new JTextField();
        urlTextFiled.setColumns(1000);
        urlTextFiled.setEditable(false);
        urlTextFiled.setEnabled(true);
        panel2.add(urlTextFiled, cc.xy(5, 1, CellConstraints.FILL, CellConstraints.CENTER));
        arrowImage = new JLabel();
        arrowImage.setIcon(new ImageIcon(getClass().getResource("/arrowUpSmall.png")));
        arrowImage.setText("");
        arrowImage.putClientProperty("html.disable", Boolean.FALSE);
        panel2.add(arrowImage, cc.xy(5, 5, CellConstraints.CENTER, CellConstraints.DEFAULT));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:max(d;10dlu):noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,center:d:noGrow,left:4dlu:noGrow,left:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:m:noGrow,right:d:noGrow,left:10dlu:noGrow,fill:max(d;4px):noGrow", "center:30px:grow"));
        rootPanel.add(panel3, cc.xy(2, 7, CellConstraints.FILL, CellConstraints.BOTTOM));
        final JLabel label3 = new JLabel();
        label3.setText("Application Port:");
        panel3.add(label3, cc.xy(3, 1));
        saveAndRestartApplicationButton = new JButton();
        saveAndRestartApplicationButton.setEnabled(true);
        saveAndRestartApplicationButton.setText("Save and Restart Application");
        saveAndRestartApplicationButton.putClientProperty("html.disable", Boolean.TRUE);
        panel3.add(saveAndRestartApplicationButton, cc.xy(11, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        portInputField = new JTextField();
        portInputField.setColumns(5);
        portInputField.setHorizontalAlignment(2);
        portInputField.setToolTipText("Port number range between 1023 and 65535");
        panel3.add(portInputField, cc.xy(5, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        portErrorMessage = new JLabel();
        portErrorMessage.setForeground(new Color(-65529));
        portErrorMessage.setHorizontalAlignment(2);
        portErrorMessage.setHorizontalTextPosition(2);
        portErrorMessage.setText("Choose a different port and save the change");
        portErrorMessage.setVisible(true);
        panel3.add(portErrorMessage, cc.xy(7, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        statusLabel = new JLabel();
        Font statusLabelFont = this.$$$getFont$$$("SansSerif", Font.BOLD, 28, statusLabel.getFont());
        if (statusLabelFont != null) statusLabel.setFont(statusLabelFont);
        statusLabel.setIcon(new ImageIcon(getClass().getResource("/loading.gif")));
        statusLabel.setText(" Starting Testerum Server");
        rootPanel.add(statusLabel, cc.xy(2, 6, CellConstraints.CENTER, CellConstraints.TOP));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
