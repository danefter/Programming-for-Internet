

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EmailSender extends JFrame {
    private JTextField host = new JTextField("smtp.gmail.com");
    private JTextField username = new JTextField("danieleinarjensen@gmail.com");
    private JTextField password = new JTextField("PASSWORD NOT INCLUDED");
    private JTextField from = new JTextField("danieleinarjensen@gmail.com");
    private JTextField to = new JTextField("danieleinarjensen@gmail.com");
    private JTextField subject = new JTextField();
    private JTextArea message = new JTextArea();
    private JButton sendButton;

    public static void main(String[] var0) {
        new EmailSender();
    }

    public EmailSender() {
        this.setDefaultCloseOperation(3);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("Server:"));
        panel.add(this.host);
        panel.add(new JLabel("Username:"));
        panel.add(this.username);
        panel.add(new JLabel("Password:"));
        panel.add(this.password);
        panel.add(new JLabel("From:"));
        panel.add(this.from);
        panel.add(new JLabel("To:"));
        panel.add(this.to);
        panel.add(new JLabel("Subject:"));
        panel.add(this.subject);
        sendButton = new JButton("Send");
        sendButton.addActionListener(new Sender());
        this.getContentPane().add("North", panel);
        this.getContentPane().add("South", sendButton);
        this.getContentPane().add("Center", new JScrollPane(this.message));
        this.setSize(640, 400);
        this.setVisible(true);
    }

    public synchronized void send() {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", this.host.getText());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(properties,new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username.getText(), password.getText());
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            InternetAddress address = new InternetAddress(this.from.getText());
            message.setFrom(address);
            InternetAddress[] addresses = new InternetAddress[]{new InternetAddress(this.to.getText())};
            message.setRecipients(RecipientType.TO, addresses);
            message.setSubject(this.subject.getText());
            message.setText(this.message.getText());
            Transport.send(message, message.getAllRecipients());
        } catch (MessagingException e) {
            System.out.println("MessagingException generated" + e);
        }

    }

    class Sender implements ActionListener {
        Sender() {
        }

        public void actionPerformed(ActionEvent var1) {
            EmailSender.this.send();
        }
    }
}
