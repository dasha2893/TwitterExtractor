
import ru.stachek66.nlp.mystem.holding.Factory;
import ru.stachek66.nlp.mystem.holding.MyStem;
import ru.stachek66.nlp.mystem.holding.MyStemApplicationException;
import ru.stachek66.nlp.mystem.holding.Request;
import ru.stachek66.nlp.mystem.model.Info;
import scala.Option;
import scala.collection.JavaConversions;

import java.io.File;
import java.text.ParseException;


/**
 * Created by dashab on 23.04.17.
 */
public class Test {
    public static void main(String[] args) throws ParseException {
        File file = new File ("mystem");
        MyStem mystemAnalyzer =
                new Factory("-igd --eng-gr --format json --weight")
                        .newMyStem("3.0", Option.apply(file)).get();
        String tweet ="к";
        Iterable<Info> result = null;
        try {
            result = JavaConversions.asJavaIterable(
                    mystemAnalyzer.analyze(Request.apply(tweet)).info().toIterable()
            );
        } catch (MyStemApplicationException e) {
            e.printStackTrace();
        }

        for (final Info info : result) {
            System.out.println(info.initial() + " -> " + info.lex() + " | " + info.rawResponse());
        }
    }
}
