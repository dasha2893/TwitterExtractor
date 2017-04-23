package TwitterExtractor.Dao.DefaultData;
import TwitterExtractor.Dao.DBConnectionInsert;
import TwitterExtractor.Dao.DBConnectionUpdate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class PostgreEmoticons {

    public static void main(String[] args) throws SQLException, IOException {

        DBConnectionInsert instance = DBConnectionInsert.getInstance();
        Connection connection = instance.getConnection();
        Statement statement = connection.createStatement();

        File file = new File("src/main/resources/emoticons.txt");

        LineIterator iterator = FileUtils.lineIterator(file, "UTF-8");
        try {
            while (iterator.hasNext()) {
                String resultLine = iterator.nextLine().trim();
                String[] strings = resultLine.split(" ");
                if(strings[1].equals("+")) strings[1]="1";
                else strings[1]="-1";

                statement.execute(("INSERT INTO emoticons (emoticon, mark) VALUES ('" + strings[0].replace("'","''") + "', '" + strings[1]+ "')").toString());

            }
        } finally {
                LineIterator.closeQuietly(iterator);
        }

        connection.close();

    }
}
