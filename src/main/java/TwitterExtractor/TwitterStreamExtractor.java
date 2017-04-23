package TwitterExtractor;

import TwitterExtractor.Dao.DBConnectionInsert;
import TwitterExtractor.Dao.DBConnectionUpdate;
import TwitterExtractor.objects.Account;
import TwitterExtractor.objects.SentimentPost;
import TwitterExtractor.objects.StreamTask;
import TwitterExtractor.objects.WordInSelection;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TwitterStreamExtractor {
    final List<String> listTopics = Arrays.asList("спорт","музыка","новости","политикаа");
    final List<Account> accounts = DBConnectionInsert.getAccounts();

    private List<StreamTask> getStreamTasks() {
        List<StreamTask> streamTasks = new ArrayList<>();
        for (int i = 0; i < listTopics.size(); i++) {
            streamTasks.add(new StreamTask(listTopics.get(i), accounts.get(i)));
        }
        return streamTasks;
    }

    public void start() {

        for (StreamTask streamTask : getStreamTasks()) {
            TwitterTemplate twitter = new TwitterTemplate(streamTask.account.getConsumerKey(),
                    streamTask.account.getConsumerSecret(),
                    streamTask.account.getAccessToken(),
                    streamTask.account.getAccessTokenSecret()
            );
            twitter.streamingOperations().filter(streamTask.topic, Arrays.asList(new TwitterStreamListener()));
            //twitter.streamingOperations().sample( Arrays.asList(new TwitterStreamListener()));

        }
    }

    public static void main(String[] args) throws SQLException {
        new TwitterStreamExtractor().start();

        DBConnectionUpdate instance = DBConnectionUpdate.getInstance();
        Connection connection = instance.getConnection();
        Statement statement = connection.createStatement();
        Statement statement1 = connection.createStatement();

        double countPosNegTest = 0;
        ResultSet resultSet = statement.executeQuery("select count(*) from posts where to_date(date,'YYYY-MM-DD') < current_date");
        if(resultSet.next()) countPosNegTest = Double.parseDouble(resultSet.getString(1));
        statement.close();

        for (int i=1; i<=countPosNegTest; i++){
            statement1 = connection.createStatement();
            ResultSet resultQuery = statement1.executeQuery("select * from (select p.*, row_number() over (order by id) rn from posts p where to_date(date,'YYYY-MM-DD') < current_date)t where t.rn=" + i);
            String tweet = "";
            int id = 0;
            if(resultQuery.next()) {
                tweet = resultQuery.getString(2);
                id = resultQuery.getInt(1);
                System.out.println(tweet);
            }
            System.out.println(tweet);

            SentimentPost tweetWithoutEmoticons = TextProcessing.deleteEmoticons(tweet);
            SentimentPost tweetAndArrayWords = TextProcessing.getWordsFromTweet(tweetWithoutEmoticons);
            WordInSelection valueForTonalityClass = SentimentAnalysis.calculateValueForTonalityClass(tweetAndArrayWords.getArrayWords());
            DBConnectionUpdate.updateTypeOfTweet(valueForTonalityClass, new SentimentPost(tweet, tweetWithoutEmoticons.getMarkOfEmoticons(), tweetAndArrayWords.getArrayWords(), id));

        }
    }
}
