package TwitterExtractor;

import TwitterExtractor.Dao.DBConnectionUpdate;
import TwitterExtractor.objects.JSONMorphProperties;
import TwitterExtractor.objects.JSONWordAndMorphProp;
import TwitterExtractor.objects.SentimentPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextProcessing {
    private final static Logger logger = Logger.getLogger(TextProcessing.class);
    private final static File file = new File ("mystem");
    private  final static ArrayList<String> frequencyArray = addWordsToFrequencyArray();
    private final static MyStem mystemAnalyzer =
            new Factory("-igd --eng-gr --format json --weight")
                    .newMyStem("3.0", Option.apply(file)).get();

    public static ArrayList<String> addWordsToFrequencyArray()  {
        File file = new File("src/main/resources/frequency_dictionary.txt");
        ArrayList<String> frequencyArray = new ArrayList<String>();
        LineIterator iterator = null;
        try {
            iterator = FileUtils.lineIterator(file, "UTF-8");
            while (iterator.hasNext()) {
                frequencyArray.add(iterator.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
         finally {
            LineIterator.closeQuietly(iterator);
        }
        return frequencyArray;

    }

    public static SentimentPost deleteEmoticons(String str) throws SQLException {
        logger.info("come into deleteEmoticons()");
        String tweet = "";
        Integer mark = 0;
        int countOpeningBracket = 0;
        int countСlosingBracket = 0;


        DBConnectionUpdate instance = DBConnectionUpdate.getInstance();
        Connection connection = instance.getConnection();
        Statement statement = connection.createStatement();

        str = str.replaceAll("((http|https|ftp):\\S+)|(@[A-Za-z-\\_]+)|(\\d+[A-Za-z]*)|(#[A-Za-z-\\_]+)|(#[А-Яа-я-\\_]+)", "");

        ResultSet resultQuery = statement.executeQuery("SELECT delete_emoticons('" + str + "')");
        if (resultQuery.next()) {
            String result = resultQuery.getString(1);
            String[] tweetAndSumMark = result.split("sum_of_mark=");
            tweet = tweetAndSumMark[0];
            mark = mark + Integer.valueOf(tweetAndSumMark[1]);
        }
        else tweet = str;
        statement.close();

        Pattern openingBracket = Pattern.compile("\\(");
        Matcher matcherOpeningBracket = openingBracket.matcher(tweet);
        while (matcherOpeningBracket.find()) countOpeningBracket++;

        Pattern closingBracket = Pattern.compile("\\)");
        Matcher matcherClosingBracket = closingBracket.matcher(tweet);
        while (matcherClosingBracket.find()) countСlosingBracket++;

        if(countOpeningBracket>countСlosingBracket || countOpeningBracket<countСlosingBracket)
        {
            tweet = tweet.replace("(","");
            mark = mark + (-1)*countOpeningBracket;

            tweet = tweet.replace(")","");
            mark = mark + countСlosingBracket;
        }

        logger.info("tweet without emojis =  "+ tweet);
        return new SentimentPost(tweet,mark);

    }

    private static ArrayList<String> deleteNotEmotionalWords (String tweet) {
        logger.info("come into deleteNotEmotionalWords()");

        Pattern pattern = Pattern.compile("(^ADVPRO.*)|(^ANUM.*)|(^APRO.*)|(^CONJ.*)|(^NUM.*)|(^PART.*)|(^PR.*)|(^SPRO.*)|(^S\\,persn.*)|(^S\\,patrn.*)|(^S\\,famn.*)");
        ArrayList<String> arrayWords = new ArrayList<String>();

        Iterable<Info> result = null;
        try {
            result = JavaConversions.asJavaIterable(
                    mystemAnalyzer.analyze(Request.apply(tweet)).info().toIterable()
            );
        } catch (MyStemApplicationException e) {
            e.printStackTrace();
        }

        if (result != null) {
            for (Info analysisOfWord : result) {
                System.out.println(analysisOfWord.initial() + " -> " + analysisOfWord.lex() + " | " + analysisOfWord.rawResponse());
                String jsonMorphPropertiesOfWord = analysisOfWord.rawResponse().replace(".*Some\\(\\S+\\)\\s|\\s","");

                ObjectMapper mapper = new ObjectMapper();
                JSONWordAndMorphProp jsonWordAndMorphProp = null;
                try {
                    jsonWordAndMorphProp = mapper.readValue(jsonMorphPropertiesOfWord, JSONWordAndMorphProp.class);
                    ArrayList<JSONMorphProperties> JSONMorphProperties = jsonWordAndMorphProp.getJSONMorphProperties();
                    for (JSONMorphProperties morphPropertiesOfWord : JSONMorphProperties) {
                        Matcher matcher = pattern.matcher(morphPropertiesOfWord.getCategories());
                        if(matcher.find()) tweet = tweet.replace(analysisOfWord.initial(),"");
                        else arrayWords.add(morphPropertiesOfWord.getLexeme());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        for (String frWord : frequencyArray) {
            if(arrayWords.contains(frWord)) arrayWords.remove(frWord);
        }

        return arrayWords;

    }

    public static SentimentPost getWordsFromTweet(SentimentPost tweetAndMark) {
        logger.info("come into getWordsFromTweet()");

        String text = tweetAndMark.getTextPost();

        boolean strengthening = false;
        Pattern pattern = Pattern.compile("\\!{2,}|\\?{2,}");
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) strengthening=true;

        String tweet = text.replaceAll("\\p{Punct}", " ").
                replaceAll("[A-Za-z]+","").
                replaceAll("[\\.\\-\\—\\*]+"," ").
                replace("ё","е").
                replaceAll("\\s{2,}", " ").toLowerCase();


        ArrayList<String> arrayList = deleteNotEmotionalWords(tweet);

        return new SentimentPost(tweet,tweetAndMark.getMarkOfEmoticons(),arrayList, strengthening);

    }


}
