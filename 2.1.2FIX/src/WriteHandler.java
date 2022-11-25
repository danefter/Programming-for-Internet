import java.io.*;
import java.net.*;


public class WriteHandler extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteHandler(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("OUTPUT STREAM ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            Console console = System.console();
            String userName = console.readLine("\nEnter your name: ");
            client.setUserName(userName);
            writer.println(userName);
        String text = "";
        while (text != null && !text.equals("exit")) {
            text = console.readLine("[" + userName + "]: ");
            writer.println(text);
        }
            writer.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("WRITE ERROR: " + ex.getMessage());
        }
    }
}