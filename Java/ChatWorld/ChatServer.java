import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Could not start the server on port 12345");
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String clientName;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                while (true) {
                    out.println("ENTER_NAME");
                    clientName = in.readLine();

                    if (clientName == null) {
                        return;
                    }

                    synchronized (clientWriters) {
                        boolean nameExists = clientWriters.stream().anyMatch(writer -> writer.equals(out));

                        if (!nameExists && !clientName.trim().isEmpty()) {
                            clientWriters.add(out);
                            break;
                        } else {
                            out.println("NAME_TAKEN");
                        }
                    }
                }

                out.println("NAME_ACCEPTED " + clientName);
                broadcastMessage("SERVER", clientName + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (!message.trim().isEmpty()) {
                        broadcastMessage(clientName, message);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client " + clientName);
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        clientWriters.remove(out);
                    }
                    if (clientName != null) {
                        broadcastMessage("SERVER", clientName + " has left the chat.");
                    }
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing connection for client " + clientName);
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String sender, String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    if (writer != out) {
                        writer.println(sender + ": " + message);
                    }
                }
            }
        }
    }
}
