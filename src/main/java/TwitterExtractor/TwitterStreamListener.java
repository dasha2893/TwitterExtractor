package TwitterExtractor;

import TwitterExtractor.Dao.DBConnectionInsert;
import TwitterExtractor.Dao.DBConnectionUpdate;
import org.apache.log4j.Logger;
import org.springframework.social.twitter.api.*;
import java.io.Serializable;
import java.sql.SQLException;


public class TwitterStreamListener implements StreamListener, Serializable {

    private final Logger logger = Logger.getLogger(TwitterStreamListener.class);


    @Override
    public void onTweet(Tweet tweet) {
        if(tweet.getLanguageCode().equals("ru")){
            logger.info("tweet = " + tweet.getText());

            try {
                DBConnectionInsert.saveTweet(tweet);
            } catch (SQLException e) {
                logger.error("onTweet() : saveTweet()");
                e.printStackTrace();
            }
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
