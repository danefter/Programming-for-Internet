import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuestBook extends JFrame
{
    private static final String mySQLServer = "atlas.dsv.su.se";
    private static final String mySQLDatabase = "";
    private static final String mySQLUsername = "";
    private static final String mySQLPassword = "";

    private String url;
    private Statement statement;
    private JTextField nameText;
    private JTextField emailText;
    private JTextField homepageText;
    private JTextField commentText;
    private JButton addButton;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    public static void main(String[] args) {
        new GuestBook();
    }

    public GuestBook(){
        super("Guest Book" );
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.url = "jdbc:mysql://" + mySQLServer + "/" + mySQLDatabase + "?user="
                + mySQLUsername + "&password=" + mySQLPassword;
        this.statement = null;
        this.nameText = new JTextField(40);
        nameText.setHorizontalAlignment(SwingConstants.LEFT);
        this.emailText = new JTextField(40);
        emailText.setHorizontalAlignment(SwingConstants.LEFT);
        this.homepageText = new JTextField(40);
        homepageText.setHorizontalAlignment(SwingConstants.LEFT);
        this.commentText = new JTextField(80);
        commentText.setHorizontalAlignment(SwingConstants.LEFT);
        this.addButton = new JButton("CLICK TO ADD");
        addButton.setHorizontalAlignment(SwingConstants.LEFT);
        this.textArea = new JTextArea();
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                String name = GuestBook.this.removeHTML(GuestBook.this.nameText.getText());
                String email = GuestBook.this.removeHTML(GuestBook.this.emailText.getText());
                String homepage = GuestBook.this.removeHTML(GuestBook.this.homepageText.getText());
                String comment = GuestBook.this.removeHTML(GuestBook.this.commentText.getText());
                GuestBook.this.addComment(name, email, homepage, comment);
                GuestBook.this.showComments();
                GuestBook.this.commentText.setText("");
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 0));
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(nameLabel);
        panel.add(this.nameText);
        JLabel emailLabel = new JLabel("E-mail:");
        emailLabel.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(emailLabel);
        panel.add(this.emailText);
        JLabel homeLabel = new JLabel("Homepage:");
        homeLabel.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(homeLabel);
        panel.add(this.homepageText);
        JLabel comLabel = new JLabel("Comment:");
        comLabel.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(comLabel);
        panel.add(this.commentText);
        JLabel addLabel = new JLabel("Add:");
        addLabel.setHorizontalAlignment(SwingConstants.LEADING);
        panel.add(addLabel);
        panel.add(this.addButton);
        scrollPane = new JScrollPane();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(new Color( 255, 255, 240 ));
        textArea.setForeground(Color.BLACK);
        textArea.setWrapStyleWord(true);
        scrollPane.getViewport().add(textArea);
        this.getContentPane().add("North", panel);
        this.getContentPane().add("Center", scrollPane);
        this.setLocation(0, 0);
        this.setSize(640, 480);
        this.setVisible(true);
        this.connect();
        this.showComments();
    }
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, mySQLUsername, mySQLPassword);
            this.statement = connection.createStatement();
            String update = "CREATE TABLE IF NOT EXISTS guestbook (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, time TINYTEXT, name TINYTEXT, email TINYTEXT, homepage TINYTEXT, comment TINYTEXT)";
            this.statement.executeUpdate(update);
            this.setTitle("CONNECTED TO MySQL ON: " + mySQLServer);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FAILED CONNECTION OR TABLE CREATION");
        }

    }

    private void addComment(String name, String email, String homepage, String comment) {
        String time = this.getTime();

        try {
            String update = "INSERT INTO guestbook (time, name, email, homepage, comment) VALUES ('" + time + "', '" + name + "', '" + email + "', '" + homepage + "', '" + comment + "')";
            this.statement.executeUpdate(update);
        } catch (Exception e) {
            System.out.println("FAILED QUERY");
        }

    }

    private void showComments() {
        resetComments(this.textArea, this.statement);

    }

    void resetComments(JTextArea textArea, Statement statement) {
        nameText.setText("");
        emailText.setText("");
        homepageText.setText("");
        commentText.setText("");
        textArea.setText("");

        try {
            String query = "SELECT * FROM guestbook ORDER BY time";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                String time = resultSet.getString("time");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String homepage = resultSet.getString("homepage");
                String comment = resultSet.getString("comment");
                textArea.append("NO: " + id + " TIME: " + time + "\n" + "NAME: " + name + " EMAIL: " + email + " HOMEPAGE: " + homepage + "\n" + "COMMENT: " + comment + "\n\n");
            }
        } catch (Exception e) {
            System.out.println("FAILED FETCH");
        }
    }

    private String getTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH)+ 1;
        String stringMonth = String.valueOf(month);
        if (month < 10) {
            stringMonth = "0" + stringMonth;
        }
        int date = calendar.get(Calendar.DATE);
        String stringDate = String.valueOf(date);
        if (date < 10) {
            stringDate = "0" + stringDate;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String stringHour = String.valueOf(hour);
        if (hour < 10) {
            stringHour = "0" + stringHour;
        }
        int min = calendar.get(Calendar.MINUTE);
        String stringMin = String.valueOf(min);
        if (min < 10) {
            stringMin = "0" + stringMin;
        }

        int sec = calendar.get(Calendar.SECOND);
        String stringSec = String.valueOf(sec);
        if (sec < 10) {
            stringSec = "0" + stringSec;
        }

        return year + "-" + stringMonth + "-" + stringDate + " " + stringHour + ":" + stringMin + ":" + stringSec;
    }

    private String removeHTML(String html) {
        Pattern pattern = Pattern.compile("<.*>");
        Matcher matcher = pattern.matcher(html);
        return matcher.replaceAll("CENCUR");
    }
}
