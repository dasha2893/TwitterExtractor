package TwitterExtractor.Dao;
import TwitterExtractor.objects.SentimentPost;
import TwitterExtractor.objects.WordInSelection;
import org.apache.log4j.Logger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class DBConnectionUpdate {

    private final static Logger logger = Logger.getLogger(DBConnectionUpdate.class);
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static DBConnectionUpdate instance;
    private Connection connection;
    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String username = "postgres";
    private String password = "1113";


    private DBConnectionUpdate() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            logger.error("Database Connection Creation Failed : " + ex.getMessage());
        }
    }


    public Connection getConnection() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static DBConnectionUpdate getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnectionUpdate();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnectionUpdate();
        }
        return instance;
    }

    public static void updateTypeOfTweet(WordInSelection valueForTonalityClass, SentimentPost tweet) throws SQLException {
        DBConnectionUpdate instance = DBConnectionUpdate.getInstance();
        Connection connection = instance.getConnection();

        try{
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();

            ArrayList<String> arrayWords = tweet.getArrayWords();
            Integer postId = tweet.getPostId();

            int sentimentOfTweet = 2;

            if(valueForTonalityClass == null) {
                if(tweet.getMarkOfEmoticons()>0) sentimentOfTweet = 1;
                if(tweet.getMarkOfEmoticons()<0) sentimentOfTweet = -1;
                if(tweet.getMarkOfEmoticons()==0) sentimentOfTweet = 0;
            }
            else {
                if(valueForTonalityClass.getWord().equals("positive")){
                    if(tweet.getMarkOfEmoticons()>0) {
                        sentimentOfTweet = 1;
                    }
                    if(tweet.getMarkOfEmoticons()==0 && !tweet.getStrengthening()){
                        sentimentOfTweet = 0;
                    }
                    if(tweet.getMarkOfEmoticons()==0 && tweet.getStrengthening()){
                        sentimentOfTweet = 1;
                    }
                    if(tweet.getMarkOfEmoticons()<0){
                        sentimentOfTweet = -1;
                    }
                }

                if(valueForTonalityClass.getWord().equals("negative")){
                    if(tweet.getMarkOfEmoticons()>0) {
                        sentimentOfTweet = 1;
                    }
                    else sentimentOfTweet = -1;
                }
            }
            if(sentimentOfTweet == 1){
                for (String word : arrayWords) {
                    int isPresent=0;
                    ResultSet resultSet = statement.executeQuery("select 1 from dictionary where word='" + word + "'");
                    if(resultSet.next()) isPresent=1;
                    if (isPresent==1) statement1.execute("UPDATE dictionary set count_in_positive = count_in_positive+1 where word='"+word+"'");
                    else statement1.execute("INSERT INTO dictionary(word,count_in_positive,count_in_negative,count_in_neutral) VALUES ('"+word+"',1,0,0)");
                }
                statement2.execute("UPDATE frequency_table set count_positive_tweet= count_positive_tweet+1, count_positive_words=count_positive_words+"+arrayWords.size());
            }

            if(sentimentOfTweet == 0){
                for (String word : arrayWords) {
                    int isPresent=0;
                    ResultSet resultSet = statement.executeQuery("select 1 from dictionary where word='" + word + "'");
                    if(resultSet.next()) isPresent=1;
                    if (isPresent==1) statement1.execute("UPDATE dictionary set count_in_positive = count_in_positive+1,count_in_neutral =count_in_neutral+1 where word='"+word+"'");
                    else statement1.execute("INSERT INTO dictionary(word,count_in_positive,count_in_negative,count_in_neutral) VALUES ('"+word+"',1,0,1)");
                }
                statement2.execute("UPDATE frequency_table set count_positive_tweet= count_positive_tweet+1, count_neutral_tweet=count_neutral_tweet+1, count_neutral_words=count_neutral_words+"+arrayWords.size()+",count_positive_words=count_positive_words+"+arrayWords.size());
            }

            if(sentimentOfTweet == -1){
                for (String word : arrayWords) {
                    int isPresent=0;
                    ResultSet resultSet = statement.executeQuery("select 1 from dictionary where word='" + word + "'");
                    if(resultSet.next()) isPresent=1;
                    if (isPresent==1) statement1.execute("UPDATE dictionary set count_in_negative = count_in_negative+1 where word='"+word+"'");
                    else statement1.execute("INSERT INTO dictionary(word,count_in_positive,count_in_negative,count_in_neutral) VALUES ('"+word+"',0,1,0)");
                }
                statement2.execute("UPDATE frequency_table set count_negative_tweet=count_negative_tweet+1, count_negative_words=count_negative_words+"+arrayWords.size());
            }
            logger.info("tweet.getTextPost()= " + tweet.getTextPost());
            logger.info("sentimentOfTweet= " + sentimentOfTweet);
            logger.info("tweet.getDate()= " + tweet.getDate());
            statement.execute("UPDATE posts SET type="+sentimentOfTweet +"where id="+postId);
            connection.commit();
        }
        catch (SQLException e){
            logger.info("ROLLBACK ROLLBACK ROLLBACK ROLLBACK ROLLBACK");
            connection.rollback();
            //System.out.println(e.getMessage());
        }

    }

}
