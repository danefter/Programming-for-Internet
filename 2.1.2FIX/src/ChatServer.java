import java.io.*;
import java.net.*;
import java.util.*;


public class ChatServer extends Thread{
    private ServerSocket serverSocket;
    private static int port = 2001;
    private Set<String> clientNames = new HashSet<>();
    private Vector<ClientHandler> clients = new Vector<>();
    private static int length = 1;
    private static boolean running = true;

    public ChatServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.setTitle(0);
            start();
        } catch (IOException e) {
            System.out.println("COULD NOT CONNECT: " + e);
            System.exit(1);
        }
    }

    public void run() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                this.addClient(socket);
            } catch (Exception e) {
                System.out.println("SOCKET EXCEPTION: " + e);
                running = false;
            }
        }
        }

    public static void main(String[] args) {
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
            if (args.length != 1 && args[1].equals("nv")) {
                running = false;
            }
        }
        new ChatServer();
    }

    private synchronized void addClient(Socket socket) {
        System.out.println("NEW CLIENT");
        length += 1;
        ClientHandler newClient = new ClientHandler(socket, this);
        clients.add(newClient);
        this.setTitle(clients.size());
        newClient.start();
    }


    public synchronized void addClientName(String client) {
        clientNames.add(client);
    }

    public synchronized void removeClient(String clientName, ClientHandler aClient) {
        boolean removed = clientNames.remove(clientName);
        if (removed) {
            clients.remove(aClient);
            length -=1;
            System.out.println("CLIENT " + clientName + " HAS DISCONNECTED");
        }
    }

    public static int getClientListLength(){
        return length;
    }

    public Set<String> getClientNames() {
        return this.clientNames;
    }

    public boolean hasClients() {
        return !this.clientNames.isEmpty();
    }

    public synchronized void removeClient(ClientHandler client) {
        this.clients.removeElement(client);
        this.setTitle(getClientListLength());
    }

    public synchronized void broadcast(String message, ClientHandler excludeClient) {
        System.out.println("CLIENT: " + excludeClient.getUserName() + " BROADCAST: " + message);
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                this.sendToOne(client, message);
            }
        }
    }

    private synchronized void sendToOne(ClientHandler client, String message) {
        client.sendMessage(message);
    }

    public synchronized void who(ClientHandler client) {
        System.out.println("CLIENT: " + client.getSocket().getInetAddress().getHostName() + " ASK: wwhhoo");
        for(int i = 0; i < getClientListLength(); ++i) {
            this.sendToOne(client, "WWHHOO: " + getHost(clients.get(i)));
        }
    }

    private String getHost(ClientHandler client) {
        try {
            client.getSocket().getInetAddress();
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var3) {
            return "unknown";
        }
    }

    public String getHost() {
        try {
            serverSocket.getInetAddress();
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "FAILED: GET SERVER HOST";
        }
    }

    private void setTitle(int clients) {
        System.out.println("SERVER ON: " + this.getHost() + " - PORT: " + port + " - N CLIENTS: " + clients);
    }
}