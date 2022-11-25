
import java.io.*;
import java.net.*;


public class ReadHandler extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadHandler(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("INPUT STREAM ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    public void run() {
        try {
            String text;
        while (!socket.isClosed() && (text = reader.readLine()) != null) {
                System.out.println("\n" + text);
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            }
            reader.close();
            socket.close();
        }
        catch (SocketException ex) {
            System.out.println("DISCONNECTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
