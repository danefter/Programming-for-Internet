import java.io.*;
import java.net.*;


public class ClientHandler extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    public BufferedReader reader;
    private String userName;
    private boolean isRunning;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            printUsers();
            userName = reader.readLine();
            server.addClientName(userName);
            String serverMessage = "CLIENT CONNECTED: " + userName;
            server.broadcast(serverMessage, this);
            String clientMessage = "";
            while (!clientMessage.equals("exit")){
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                if(clientMessage.equals("wwhhoo")){
                    server.who(this);
                }
                else
                server.broadcast(serverMessage, this);
            }
            server.removeClient(userName, this);
            socket.close();
            serverMessage = "CLIENT DISCONNECTED: " + userName;
            server.broadcast(serverMessage, this);
        } catch (IOException e) {
            System.out.println("SERVER ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserName() {
        return userName;
    }

    void printUsers() {
        if (server.hasClients()) {
            writer.println("Connected users: " + server.getClientNames());
        } else {
            writer.println("No other users connected");
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}