import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    String user;
    private Socket s = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private BufferedReader reader;

    public Client(String addr, int port) {
        try {
            s = new Socket(addr, port);
            System.out.println("Connected");

            // Inputting username for clarity
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter your name: ");
            user = sc.nextLine();

            // Initialize input/output streams
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(System.in));

            // String to read message from input
            String m = "";
            String send = "";

            // Keep reading until "||&&" is input
            while (!m.equals("||&&")) {
                System.out.printf("%s: ", user);
                m = reader.readLine();
                send = user + ": " + m; // Add a space and colon for clarity
                out.writeUTF(send);
            }

            // Close the connection
            sc.close();
            in.close();
            out.close();
            s.close();
        } catch (UnknownHostException u) {
            System.out.println("Unknown host: " + u.getMessage());
        } catch (IOException i) {
            System.out.println("I/O error: " + i.getMessage());
        }
    }

    public static void main(String[] args) {
        Client c = new Client("127.0.0.1", 5000);
    }
}