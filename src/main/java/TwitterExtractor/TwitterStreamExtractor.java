package TwitterExtractor;

import TwitterExtractor.Dao.MongoDao;
import TwitterExtractor.objects.Account;
import TwitterExtractor.objects.StreamTask;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TwitterStreamExtractor {
    final List<String> listTopics = Arrays.asList("sport","music","news","policy");
    MongoDao mongoDao = MongoDao.getInstance();
    final List<Account> accounts = mongoDao.getAccounts();

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

        }
    }

    public static void main(String[] args) {
        new TwitterStreamExtractor().start();
    }
}
