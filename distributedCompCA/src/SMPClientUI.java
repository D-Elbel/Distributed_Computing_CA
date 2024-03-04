import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SMPClientUI extends JFrame {
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private EchoClientHelper2 helper;

    public SMPClientUI() {
        createUI();
        connectToServer("localhost", "7");
    }

    private void createUI() {
        setTitle("Echo Client");
        setSize(400, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        messageField = new JTextField();
        messageField.setPreferredSize(new Dimension(400, 20));
        chatArea = new JTextArea();
        chatArea.setPreferredSize(new Dimension(400, 240));
        chatArea.setEditable(false);

        sendButton = new JButton("Send Message");

        sendButton.addActionListener(this::sendMessage);

        panel.add(new JScrollPane(chatArea));
        panel.add(messageField);
        panel.add(sendButton);

        add(panel);

        setVisible(true);
    }

    private void connectToServer(String host, String port) {
        try {
            helper = new EchoClientHelper2(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(ActionEvent event) {
        try {
            String message = messageField.getText();
            String response = helper.getEcho(message);
            chatArea.append("Server: " + response + "\n");
            messageField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SMPClientUI::new);
    }
}
