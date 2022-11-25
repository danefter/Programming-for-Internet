
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements Runnable {
    private Thread thread = new Thread(this);
    private boolean running = true;
    private Socket s = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private static String host = "127.0.0.1";
    private static int port = 2000;
    private JTextField textField = new JTextField();
    private JTextArea textArea = new JTextArea();


    public static void main(String[] args) {
        if (args.length != 0) {
            host = args[0];
            if (args.length != 1) {
                port = Integer.parseInt(args[1]);
            }
        }
        try {
            Server.clients.add(new Client(new Socket(host, port)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(Socket s) {
        this.textField.addActionListener(new Client.L2());
        this.getContentPane().add("North", this.textField);
        this.getContentPane().add("Center", new JScrollPane(this.textArea));
        this.setLocation(420, 0);
        this.setSize(400, 200);
        this.addWindowListener(new Client.L1());
        this.setVisible(true);
        try {
            this.s = s;
            this.setTitle("CONNECTED TO: " + host + " - ON PORT: " + port);
            this.in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
            this.out = new PrintWriter(this.s.getOutputStream(), true);
        } catch (IOException var2) {
            System.out.println("COULD NOT CONNECT: " + var2);
            this.killMe();
        }
        this.thread.start();
    }

    public void run() {
        String s = "";
        while(this.running) {
            try {
                s = this.in.readLine();
                this.textArea.append(s + "\n");
                this.textArea.setCaretPosition(this.textArea.getText().length());
            } catch (Exception var3) {
                System.out.println("Exception generated: " + var3);
            }
        }

    }

    private void sendMessage(String var1) {
        for (Client client: Server.clients){
            client.out.println(var1);
        }
        if (var1.equals("exit")) {
            this.killMe();
        }

    }

    private String getHost(Socket socket) {
        try {
            socket.getInetAddress();
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var3) {
            return "unknown";
        }
    }

    public void killMe() {
        this.running = false;
        this.dispose();

        try {
            this.out.close();
            this.in.close();
            this.s.close();
        } catch (Exception var2) {
            System.out.println("Exception generated: " + var2);
        }

        System.exit(1);
    }
    class L1 extends WindowAdapter {

        L1() {

        }

        public void windowClosing(WindowEvent var1) {
            thread.interrupt();
        }
        public void windowClosed(WindowEvent var1) {
            System.exit(1);
        }
    }
    class L2 implements ActionListener {


        L2() {
        }

        public void actionPerformed(ActionEvent e) {
            sendMessage(Client.this.textField.getText());
            Client.this.textField.setText("");
        }
    }


}
