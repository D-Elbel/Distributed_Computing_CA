package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SMPClientUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField, messageIdField; // Added messageIdField
    private JButton sendButton, downloadButton, logOffButton, logonButton, downloadByIdButton; // Added downloadByIdButton
    private SMPClientHelper helper;
    private JPanel buttonsPanel;

    public SMPClientUI() {
        createUI();
    }

    private void createUI() {
        setTitle("Echo Client");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setText("Welcome to SMP Client. \n Please LOGON with your username and password 😀\n");
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        logonButton = new JButton("LOGON");
        logonButton.addActionListener(this::logon);

        messageField = new JTextField(25);
        sendButton = new JButton("Upload Message");
        downloadButton = new JButton("Download All Messages");
        logOffButton = new JButton("Log Off");
        downloadByIdButton = new JButton("Download by ID"); // Added downloadByIdButton
        downloadByIdButton.addActionListener(this::downloadMessageById); // Added action listener

        sendButton.addActionListener(this::sendMessage);
        downloadButton.addActionListener(this::downloadMessages);
        logOffButton.addActionListener(this::logOff);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.add(logonButton);

        buttonsPanel.add(Box.createVerticalStrut(5));
        add(buttonsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void logon(ActionEvent event) {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Username"),
                usernameField,
                new JLabel("Password"),
                passwordField
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Login", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                helper = new SMPClientHelper("localhost", "7");
                String response = helper.getEcho("LOGON " + username + " " + password);
                chatArea.append("Logon response: " + response + "\n");
                // Assuming successful login
                updateUIForLoggedInUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUIForLoggedInUser() {
        buttonsPanel.removeAll();
        buttonsPanel.add(messageField);
        buttonsPanel.add(sendButton);
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(downloadByIdButton); // Added downloadByIdButton
        buttonsPanel.add(logOffButton);
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    private void sendMessage(ActionEvent event) {
        try {
            String message = messageField.getText();
            String response = helper.getEcho("MSGUP " + message);
            chatArea.append("Server: " + response + "\n");
            messageField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadMessages(ActionEvent event) {
        try {
            String response = helper.getEcho("MSGDL");
            chatArea.append("Download Initiated: " + response + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadMessageById(ActionEvent event) {
        JTextField idField = new JTextField();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Enter Message ID"),
                idField
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Download Message by ID", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String messageId = idField.getText();
            try {
                String response = helper.getEcho("SPMSG " + messageId); // Assuming the server supports this command
                chatArea.append("Download Initiated for Message ID " + messageId + ": " + response + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void logOff(ActionEvent event) {
        try {
            String response = helper.getEcho("LGOFF");
            chatArea.append("Logged Off: " + response + "\n");

            resetUIForLoggedOutUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetUIForLoggedOutUser() {
        buttonsPanel.removeAll();
        buttonsPanel.add(logonButton);
        chatArea.setText("");
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SMPClientUI::new);
    }
}
