import java.net.*;
import java.io.*;


public class ChatClient {
    private static String hostname = "127.0.0.";
    private static int port = 2001;
    private String userName;

    public ChatClient(String hostname, int port) {
        ChatClient.hostname = hostname;
        ChatClient.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("CONNECTED TO " + hostname + " ON " + port);
            new ReadHandler(socket, this).start();
            new WriteHandler(socket, this).start();
        } catch (UnknownHostException ex) {
            System.out.println("SERVER NOT FOUND: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O ERROR: " + ex.getMessage());
        }

    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        if (args.length >= 2) {
            hostname = args[0];
            port = Integer.parseInt(args[1]);}
        ChatClient client = new ChatClient(hostname+ChatServer.getClientListLength(), port);
        client.execute();
    }
}