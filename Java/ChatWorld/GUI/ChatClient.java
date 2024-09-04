import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Chat");
    private JTextField textField = new JTextField(50);
    private JTextArea messageArea = new JTextArea(16, 50);
    private String clientName;

    public ChatClient() {
        // Apply FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        // Customize UI components
        UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("TextField.arc", 10);  // Rounded corners for text field
        UIManager.put("Button.arc", 10);     // Rounded corners for buttons

        // Layout GUI
        messageArea.setEditable(false);

        // Panel to hold the message area with padding
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the message area
        messagePanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Add the message panel at the center
        frame.getContentPane().add(messagePanel, BorderLayout.CENTER);

        // Panel to hold the text field with padding and rounded border
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true), // Rounded border with radius 10
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1, true), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )); // Rounded border for text field with inner padding

        // Add the text field to the panel
        textFieldPanel.add(textField, BorderLayout.CENTER);

        // Add the text field panel at the bottom
        frame.getContentPane().add(textFieldPanel, BorderLayout.SOUTH);

        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = textField.getText();
                out.println(message);
                messageArea.append(message + "\n"); // Display the message without the name for the sender
                textField.setText("");
            }
        });
    }

    private void run() throws IOException {
        // Connect to the server on localhost
        Socket socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Ask for the client's name once
        clientName = JOptionPane.showInputDialog(
            frame,
            "Enter your name:",
            "Name Required",
            JOptionPane.PLAIN_MESSAGE
        );
        out.println(clientName);

        // Enable the text field after the name is accepted
        textField.setEditable(true);

        // Receive messages from the server
        while (true) {
            String line = in.readLine();
            if (line != null) {
                if (line.startsWith(clientName + ":")) {
                    // Ignore the line if it starts with the client's own name
                    continue;
                } else {
                    // Display messages from other clients normally
                    messageArea.append(line + "\n");
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
