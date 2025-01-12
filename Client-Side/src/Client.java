import java.io.*;
import java.net.*;

public class Client {
    private Socket s = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private BufferedReader reader;

    public Client(String addr, int port) {
        try {
            s = new Socket(addr, port);
            System.out.println("Connected to the server");


            // Initialize input/output streams
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));

            // Start a thread to listen for messages from the server
            new Thread(new MessageListener()).start();

            // String to read message from input
            String m = "";

            // Keep reading until "||&&" is input
            while (!m.equals("||&&")) {
                m = reader.readLine();
                out.writeUTF(m);
                out.flush(); // Ensure the message is sent immediately
            }

            // Notify server of disconnection
            out.writeUTF("Client has left the chat.");
            out.flush();

            // Close the connection
            cleanup();
        } catch (UnknownHostException u) {
            System.out.println("Unknown host: " + u.getMessage());
        } catch (IOException i) {
            System.out.println("I/O error: " + i.getMessage());
        }
    }

    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (s != null) s.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    private class MessageListener implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while (true) {
                    message = in.readUTF();
                    System.out.println(message);
                }
            } catch (IOException e) {
                System.out.println("Connection closed by the server.");
            }
        }
    }

    public static void main(String[] args) {
        Client c = new Client("127.0.0.1", 5000);
    }
}