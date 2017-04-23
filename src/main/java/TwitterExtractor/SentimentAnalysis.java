package TwitterExtractor;

import TwitterExtractor.Dao.DBConnectionUpdate;
import TwitterExtractor.objects.WordInSelection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class SentimentAnalysis {
    private final static Logger logger = Logger.getLogger(SentimentAnalysis.class);

    public static ArrayList<WordInSelection> getNumberOfWordInSelection(ArrayList<String> array, int selection) throws SQLException {
        logger.info("come into getNumberOfWordInSelection()");

        DBConnectionUpdate instance = DBConnectionUpdate.getInstance();
        Connection connection = instance.getConnection();
        Statement statement = connection.createStatement();

        ArrayList<WordInSelection> wordInSelections = new ArrayList<WordInSelection>();

        String arrayWords = "";
        if (!array.isEmpty()){
            for (String w : array) {
                arrayWords = arrayWords + "'" + w + "',";
            }
            arrayWords = arrayWords.replaceAll(",$", "");
            ResultSet resultSet = statement.executeQuery("select * from getCountWordInSelection(ARRAY[" + arrayWords + "]," + selection + ")");
            while (resultSet.next()) {
                wordInSelections.add(new WordInSelection(resultSet.getString(1).trim(), resultSet.getDouble(2)));
            }
        }

        statement.close();

        return wordInSelections;


    }

    public static WordInSelection calculateValueForTonalityClass(ArrayList<String> arrayWords) throws SQLException {
        logger.info("come into calculateValueForTonalityClass()");

        if (arrayWords.isEmpty()) return null;

        double positiveValue = 0;
        double negativeValue = 0;

        DBConnectionUpdate instance = DBConnectionUpdate.getInstance();
        Connection connection = instance.getConnection();
        Statement statement = connection.createStatement();

        ResultSet resultFrequency = statement.executeQuery("select * from frequency_table");
        double countNegativeTweet = 0;
        double countPositiveTweet = 0;
        double countPositiveWords = 0;
        double countNegativeWords = 0;

        while (resultFrequency.next()){
            countNegativeTweet = Double.parseDouble(resultFrequency.getString(1));
            countPositiveTweet = Double.parseDouble(resultFrequency.getString(2));
            countPositiveWords = Double.parseDouble(resultFrequency.getString(4));
            countNegativeWords = Double.parseDouble(resultFrequency.getString(5));
        }

        long countUniqueWordsInDictionary = 0;
        Statement statement3 = connection.createStatement();
        ResultSet result_countWordsInDictionary = statement3.executeQuery("select count(*) from dictionary");
        while (result_countWordsInDictionary.next()) countUniqueWordsInDictionary=Long.valueOf(result_countWordsInDictionary.getString(1));

        //Calculate the value of the expression for the positive tone
        ArrayList<WordInSelection> positiveWordInSelections = getNumberOfWordInSelection(arrayWords, 1);

        String positiveLog = "log(" + countPositiveTweet +"/(" +countNegativeTweet + "+" + countPositiveTweet +"))";
        double positiveMarkWord = 0.;
        if (positiveWordInSelections != null) {
            for (WordInSelection positiveWordInSelection : positiveWordInSelections) {
                positiveMarkWord = positiveWordInSelection.getCountInSelection()+1.0;
                positiveLog = positiveLog + "+ log(" + positiveMarkWord +"/("+countUniqueWordsInDictionary + "+" + countPositiveWords+"))";
            }
        }
        logger.info("positiveLog = " +positiveLog);


        //Calculate the value of the expression for the negative tone
        ArrayList<WordInSelection> negativeWordInSelections = getNumberOfWordInSelection(arrayWords, -1);

        String negativeLog = "log(" + countNegativeTweet +"/(" +countNegativeTweet + "+" + countPositiveTweet +"))";
        double negativeMarkWord;
        if (negativeWordInSelections != null) {
            for (WordInSelection negativeWordInSelection : negativeWordInSelections) {
                negativeMarkWord = negativeWordInSelection.getCountInSelection()+1.0;
                negativeLog = negativeLog + "+ log(" + negativeMarkWord +"/("+countUniqueWordsInDictionary + "+" + countNegativeWords+"))";
            }
        }
        logger.info("negativeLog = " + negativeLog);


        Statement statement1 = connection.createStatement();
        ResultSet resultSet = statement1.executeQuery("select 1/(1+(neg/pos)), 1/(1+(pos/neg)) FROM" +
                "    (" +
                "      SELECT power(exp(1.0),"+negativeLog+") neg," +
                "             power(exp(1.0),"+positiveLog+") pos" +
                "    ) t");
        while (resultSet.next()) {
            positiveValue = resultSet.getDouble(1);
            negativeValue = resultSet.getDouble(2);
        }
        logger.info("positiveValue = " +positiveValue);
        logger.info("negativeValue = " +negativeValue);

        if (positiveValue > negativeValue) return new WordInSelection("positive",positiveValue);
        else return new WordInSelection("negative",negativeValue);

    }


}
