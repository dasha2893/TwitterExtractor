package TwitterExtractor.objects;

public class WordInSelection {

    private String word;
    private double countInSelection;


    public WordInSelection(String word, double countInSelection) {
        this.word = word;
        this.countInSelection = countInSelection;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getCountInSelection() {
        return countInSelection;
    }

    public void setCountInSelection(double countInSelection) {
        this.countInSelection = countInSelection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordInSelection wordInSelection1 = (WordInSelection) o;

        if (Double.compare(wordInSelection1.countInSelection, countInSelection) != 0) return false;
        return word != null ? word.equals(wordInSelection1.word) : wordInSelection1.word == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = word != null ? word.hashCode() : 0;
        temp = Double.doubleToLongBits(countInSelection);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


}
