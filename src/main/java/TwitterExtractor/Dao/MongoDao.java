package TwitterExtractor.Dao;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import TwitterExtractor.objects.Account;
import TwitterExtractor.objects.SentimentTweet;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.joda.time.DateTime;
import org.springframework.social.twitter.api.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class MongoDao {
    private final static  MongoDao mongoDao = new MongoDao();
    private final Logger logger = Logger.getLogger(MongoDao.class);

//    MongoClient mongoClient = new MongoClient("localhost");
//    MongoDatabase db = mongoClient.getDatabase("database name");
//    boolean auth = mongoClient..authenticate("username", "password".toCharArray());


    public final MongoDatabase db = new MongoClient("localhost").getDatabase("local");
    private final MongoCollection<Document> accounts = db.getCollection("accounts");
    private final MongoCollection<Document> tweets = db.getCollection("tweets");




    public static MongoDao getInstance(){
        return mongoDao;
    }

    public List<Account> getAccounts() {
        List<Account> resultAccounts = new ArrayList<>();

//        boolean auth = db.("testdb", "password".toCharArray());

        for (Document document : accounts.find()) {
            System.out.println(document);
            String consumerKey = (String) document.get("consumerKey");
            String consumerSecret = (String) document.get("consumerSecret");
            String accessToken = (String) document.get("accessToken");
            String accessTokenSecret = (String) document.get("accessTokenSecret");
            resultAccounts.add(new Account(consumerKey, consumerSecret, accessToken, accessTokenSecret));
        }
        return resultAccounts;
    }

    public void saveTweet(Tweet tweet, int sentimentTweet) {

        Document document = new Document("_id", tweet.getId())
                .append("userName", tweet.getUser().getName())
                .append("textPost", tweet.getText())
                .append("isRetweet", tweet.isRetweet())
                .append("isRetweeted", tweet.isRetweeted())
                .append("retweetCount", tweet.getRetweetCount())
                .append("createdDate", tweet.getCreatedAt().getTime())
                .append("sentimentTweet", sentimentTweet)
                .append("views", 0);
        tweets.insertOne(document);
    }

    public List<SentimentTweet> getTweets(String str, String firstDay, String lastDay) {
        List<SentimentTweet> sentimentTweetList = new ArrayList<>();
        BasicDBObject basicDBObject = new BasicDBObject();
        List<BasicDBObject> obj = new ArrayList<>();

        if(firstDay.equals("") && lastDay.equals("")){
//            basicDBObject.put("textPost", Pattern.compile(str, Pattern.CASE_INSENSITIVE));
            basicDBObject.put("textPost", Pattern.compile(str));

//            basicDBObject.put("textPost", Pattern.compile("/.*"+str+"*./", Pattern.CASE_INSENSITIVE));

        }
        if(!firstDay.equals("") && !lastDay.equals("")){
            long convertedFirstDay = new DateTime(firstDay).getMillis();
            long convertedLastDay = new DateTime(lastDay).plusDays(1).getMillis();
            obj.add(new BasicDBObject("textPost", Pattern.compile(str, Pattern.CASE_INSENSITIVE)));
            obj.add(new BasicDBObject("createdDate", new BasicDBObject("$gte", convertedFirstDay).append("$lt",convertedLastDay)));
            basicDBObject.put("$and", obj);
        }
        if(!firstDay.equals("") && lastDay.equals("")){
            long convertedFirstDay = new DateTime(firstDay).getMillis();
            obj.add(new BasicDBObject("textPost", Pattern.compile(str, Pattern.CASE_INSENSITIVE)));
            obj.add(new BasicDBObject("createdDate", new BasicDBObject("$gte", convertedFirstDay)));
            basicDBObject.put("$and", obj);
        }

        if(firstDay.equals("") && !lastDay.equals("")){
            long convertedLastDay = new DateTime(lastDay).plusDays(1).getMillis();
            obj.add(new BasicDBObject("textPost", Pattern.compile(str, Pattern.CASE_INSENSITIVE)));
            obj.add(new BasicDBObject("createdDate", new BasicDBObject("$lt", convertedLastDay)));
            basicDBObject.put("$and", obj);
        }

        FindIterable<Document> documents = tweets.find(basicDBObject);
        for (Document document : documents) {
            String textPost = (String) document.get("textPost");
            int sentimentResult = (int) document.get("sentimentTweet");
            sentimentTweetList.add(new SentimentTweet(textPost, sentimentResult));
            logger.info("textPost= " + textPost);
            logger.info("sentimentResult= " + sentimentResult);
        }
        return sentimentTweetList;
    }


}
