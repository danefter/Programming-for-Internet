
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static boolean running = true;
    private static int port = 2000;
    public static ServerSocket serverSocket = null;
    public static ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(port);
            acceptClients();
        } catch (IOException e) {
            System.out.println("COULD NOT CONNECT: " + e);
            running = false;
        }
    }

    public static void acceptClients(){
        while(running){
            try {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                clients.add(client);
            } catch (IOException e) {
                System.out.println("COULD NOT CONNECT: " + e);
            }
        }
    }
}
