import java.io.*;
import java.net.*;
import java.util.*;
import com.formdev.flatlaf.FlatLightLaf;

public class ChatServer {
    private static Set<Socket> clientSockets = new HashSet<>();
    private static Set<String> clientNames = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Chat server started...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String clientName;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Ask for the client's name
                while (true) {
                    out.println("Enter your name:");
                    clientName = in.readLine();
                    if (clientName == null || clientName.isEmpty() || clientNames.contains(clientName)) {
                        out.println("Name already taken or invalid. Try another name.");
                    } else {
                        clientNames.add(clientName);
                        break;
                    }
                }
                out.println("Welcome " + clientName + "!");
                synchronized (clientSockets) {
                    clientSockets.add(socket);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(clientName + ": " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientSockets) {
                    clientSockets.remove(socket);
                    clientNames.remove(clientName);
                }
                broadcastMessage(clientName + " has left the chat.");
            }
        }

        private void broadcastMessage(String message) {
            System.out.println(message);
            synchronized (clientSockets) {
                for (Socket clientSocket : clientSockets) {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
