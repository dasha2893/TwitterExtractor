package TwitterExtractor.Dao;


import TwitterExtractor.objects.Account;
import org.apache.log4j.Logger;
import org.springframework.social.twitter.api.Tweet;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBConnectionInsert {

    private final static Logger logger = Logger.getLogger(DBConnectionInsert.class);
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static DBConnectionInsert instance;
    private Connection connection;
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String username = "";
    private String password = "";


    private DBConnectionInsert() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            logger.error("Database Connection Creation Failed : " + ex.getMessage());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DBConnectionInsert getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnectionInsert();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnectionInsert();
        }
        return instance;
    }

    public static List<Account> getAccounts() {
        List<Account> resultAccounts = new ArrayList<>();

        try {
            DBConnectionInsert instance = DBConnectionInsert.getInstance();
            Connection connection = instance.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from accounts limit 1");
            while (resultSet.next()){
                resultAccounts.add(new Account(resultSet.getString(1).trim(),resultSet.getString(2).trim(),resultSet.getString(3).trim(),resultSet.getString(4).trim()));
            }

            connection.close();

        } catch (SQLException e) {
            logger.error("method: getAccounts()");
            e.printStackTrace();
        }

        return resultAccounts;
    }


    public static void saveTweet(Tweet tweet) throws SQLException {
        DBConnectionInsert instance = DBConnectionInsert.getInstance();
        Connection connection = instance.getConnection();
        connection.setAutoCommit(true);
        Statement statement = connection.createStatement();

        String post = tweet.getText();

        java.util.Date date = tweet.getCreatedAt();
        String formatedDate = sdf.format(date);

        statement.execute("INSERT INTO tweets (text, date) VALUES ('"+post+"', '"+formatedDate+"')");
    }
}
