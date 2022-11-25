import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Client extends JFrame {
    private DatagramSocket socket = null;
    private static int myPort = 2000;
    private static int remotePort = 2001;
    private DrawPane drawPane;
    private static InetAddress host;
    private boolean running = true;

    public static void main(String[] args) {
        if (args.length >= 1) {
            myPort = Integer.parseInt(args[0]);
            if (args.length >= 2) {
                try {
                    host = InetAddress.getByName(args[1]);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (args.length >= 3) {
                    remotePort = Integer.parseInt(args[2]);
            }
        }
    }
        new Client();
    }

    public Client() {
        this.setLocation(420, 0);
        this.setSize(800, 400);
        this.addWindowListener(new WindowCloser());
        this.drawPane = new DrawPane(Color.BLACK, 2);
        this.setContentPane(this.drawPane);
        this.setVisible(true);
        try {
            if(host == null)
                host = InetAddress.getByName("localhost");
            this.socket = new DatagramSocket(myPort, host);
            this.setTitle("CONNECTED TO: " + host + " - ON PORT: " + myPort);
            while(true) {
                byte[] buf = (100 + " " + 100).getBytes();
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivePacket);
                recieveMessage(receivePacket);}
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        }


    public DatagramPacket sendMessage(Point p) {
        String message = p.x + " " + p.y;
        byte[] buf = message.getBytes();
        return new DatagramPacket(buf, buf.length, host, remotePort);
    }

    private void recieveMessage(DatagramPacket packet) {
        byte[] msgBuffer = packet.getData();
        int length = packet.getLength();
        int offset = packet.getOffset();
        String message = new String(msgBuffer, offset, length);
        String[] xy = message.split(" ");
        Point p = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
        Circle newCircle = new Circle(drawPane.getCircleSize(), p);
        drawPane.addCircle(newCircle);
        repaint();
    }

    public void killMe() {
        this.dispose();
        try {
            running = false;
            this.socket.close();
        } catch (Exception e) {
            System.out.println("Exception generated: " + e);
        }
        System.exit(1);
    }

    static private class Circle
    {
        private int size;
        private Point center;
        Circle(int size, Point point)
        {
            setSize(size);
            setLocation(point);
        }

        void setSize(int size) {
            this.size = Math.max(size, 1);
        }

        void setLocation(Point point) {
            center = point;
        }

        int getSize()
        {
            return size;
        }

        Point getCenter()
        {
            return center;
        }


        public void draw(Graphics g)
        {
            g.fillOval(getCenter().x,getCenter().y,getSize(),getSize());
        }
    }

    class WindowCloser extends WindowAdapter {

        WindowCloser() {

        }

        public void windowClosing(WindowEvent var1) {
            drawPane.getGraphics().dispose();
        }
        public void windowClosed(WindowEvent var1) {
            killMe();
            System.exit(1);
        }
    }
    private class DrawPane extends JPanel implements MouseInputListener {

        private int circleDiameter;
        private ArrayList <Circle> circleArrayList = new ArrayList<Circle>();

        DrawPane(Color color, int size) {
            setCircleColor(color);
            setCircleDiameter(size);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Iterator<Circle> circleIterator = circleArrayList.iterator();
            Circle drawCircle;
            while(circleIterator.hasNext())
            {
                drawCircle = (Circle) circleIterator.next();
                drawCircle.draw(g);
            }
        }

        public void setCircleDiameter(int tempSize)
        {
            circleDiameter = tempSize;
        }

        public void setCircleColor(Color tempColor)
        {
        }

        public int getCircleSize()
        {
            return circleDiameter;
        }


        public void clear() {
            circleArrayList.clear();
            repaint();
            JOptionPane.showMessageDialog(null, "Please restart the program");

        }

        public void addCircle(Circle c){
            circleArrayList.add(c);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                socket.send(sendMessage(e.getPoint()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Circle newCircle = new Circle(getCircleSize(), e.getPoint());
            circleArrayList.add( newCircle );
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseDragged(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


}
