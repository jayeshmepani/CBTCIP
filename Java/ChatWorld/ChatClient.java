import java.io.*;
import java.net.*;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader consoleInput;
    private String clientName;

    public ChatClient(String serverAddress) {
        try {
            Socket socket = new Socket(serverAddress, 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            consoleInput = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

            new Thread(new ServerHandler()).start();
            new Thread(new UserInputHandler()).start();

        } catch (IOException e) {
            System.err.println("Unable to connect to server");
            e.printStackTrace();
        }
    }

    private class ServerHandler implements Runnable {
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    if (serverMessage.startsWith("ENTER_NAME")) {
                        System.out.print("Enter your name: ");
                        clientName = consoleInput.readLine();
                        out.println(clientName);
                    } else if (serverMessage.startsWith("NAME_ACCEPTED")) {
                        System.out.println("You have joined the chat as " + clientName);
                    } else if (serverMessage.startsWith("NAME_TAKEN")) {
                        System.out.println("Name already taken. Please enter a different name.");
                        System.out.print("Enter your name: ");
                        clientName = consoleInput.readLine();
                        out.println(clientName);
                    } else {
                        System.out.println(serverMessage);
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection to server lost");
                e.printStackTrace();
            }
        }
    }

    private class UserInputHandler implements Runnable {
        public void run() {
            try {
                String userInput;
                while ((userInput = consoleInput.readLine()) != null) {
                    if (!userInput.trim().isEmpty()) {
                        out.println(userInput);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading user input");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Connecting to chat server...");
        new ChatClient("127.0.0.1");
    }
}
