import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class Server {

    private static ServerSocket ss = null;
    private static final int MAX_CLIENTS = 2;
    private static final ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
    private static Socket[] clients = new Socket[MAX_CLIENTS];
    private static DataInputStream[] inStreams = new DataInputStream[MAX_CLIENTS];
    private static DataOutputStream[] outStreams = new DataOutputStream[MAX_CLIENTS];

    public Server(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for clients ...");

            for (int i = 0; i < MAX_CLIENTS; i++) {
                clients[i] = ss.accept();
                System.out.println("Client " + (i + 1) + " accepted");
                inStreams[i] = new DataInputStream(new BufferedInputStream(clients[i].getInputStream()));
                outStreams[i] = new DataOutputStream(clients[i].getOutputStream());
                pool.execute(new ClientHandler(i));
            }

            System.out.println("Both clients connected. They can now interact.");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private class ClientHandler implements Runnable {
        private int clientId;

        public ClientHandler(int clientId) {
            this.clientId = clientId;
        }

        @Override
        public void run() {
            String message = "";
            try {
                while (!message.equals("Over")) {
                    try {
                        message = inStreams[clientId].readUTF();
                        System.out.println("Client " + (clientId + 1) + ": " + message);
                        // Send the message to the other client
                        int otherClientId = (clientId == 0) ? 1 : 0;
                        sendMessageToClient(otherClientId, message);
                    } catch (IOException e) {
                        System.out.println("Client " + (clientId + 1) + " disconnected.");
                        break;
                    }
                }
            } finally {
                cleanup(clientId);
            }
        }

        private void sendMessageToClient(int clientId, String message) {
            try {
                outStreams[clientId].writeUTF(message);
                outStreams[clientId].flush();
            } catch (IOException e) {
                System.out.println("Error sending message to Client " + (clientId + 1) + ": " + e);
            }
        }

        private void cleanup(int clientId) {
            try {
                if (clients[clientId] != null) {
                    clients[clientId].close();
                }
                if (inStreams[clientId] != null) {
                    inStreams[clientId].close();
                }
                if (outStreams[clientId] != null) {
                    outStreams[clientId].close();
                }
                System.out.println("Closed connection for Client " + (clientId + 1));
            } catch (IOException e) {
                System.out.println("Error closing connection for Client " + (clientId + 1) + ": " + e);
            }
        }
    }

    public static void main(String args[]) {
        Server s = new Server(5000);
    }
}