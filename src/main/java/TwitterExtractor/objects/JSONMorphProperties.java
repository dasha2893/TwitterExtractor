package TwitterExtractor.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JSONMorphProperties {

    private String categories;
    private double weight;
    private String lexeme;
    private String qual;

    @JsonCreator
    public JSONMorphProperties(@JsonProperty("qual") String qual , @JsonProperty("gr") String categories, @JsonProperty("wt") double weight, @JsonProperty("lex") String lexeme) {
        this.categories = categories;
        this.weight = weight;
        this.lexeme = lexeme;
        this.qual = qual;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getQual() {
        return qual;
    }

    public void setQual(String qual) {
        this.qual = qual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JSONMorphProperties that = (JSONMorphProperties) o;

        if (Double.compare(that.weight, weight) != 0) return false;
        if (categories != null ? !categories.equals(that.categories) : that.categories != null) return false;
        return lexeme != null ? lexeme.equals(that.lexeme) : that.lexeme == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = categories != null ? categories.hashCode() : 0;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (lexeme != null ? lexeme.hashCode() : 0);
        return result;
    }
}
