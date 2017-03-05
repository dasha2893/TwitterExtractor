package TwitterExtractor;

import TwitterExtractor.Dao.MongoDao;
import org.apache.log4j.Logger;
import org.springframework.social.twitter.api.*;

import java.io.Serializable;


public class TwitterStreamListener implements StreamListener, Serializable {

    private final Logger logger = Logger.getLogger(TwitterStreamListener.class);
    private final SentimentAnalysis sentimentAnalysis = new SentimentAnalysis();
    MongoDao mongoDao = MongoDao.getInstance();


    @Override
    public void onTweet(Tweet tweet) {
        if(tweet.getLanguageCode().equals("en")){
            logger.info("tweet = " + tweet);
            int sentimentTweet = sentimentAnalysis.getSentimentTweet(tweet.getText());

            mongoDao.saveTweet(tweet, sentimentTweet);
        }
    }

    @Override
    public void onDelete(StreamDeleteEvent streamDeleteEvent) {

    }

    @Override
    public void onLimit(int i) {

    }

    @Override
    public void onWarning(StreamWarningEvent streamWarningEvent) {

    }

}
