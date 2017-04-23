package TwitterExtractor.objects;


import java.util.ArrayList;

public class SentimentPost {
    private Integer postId;
    private String userName;
    private String textPost;
    private int sentimentResult;
    private Long views;
    private Long date;
    private Integer markOfEmoticons;
    private boolean strengthening = false;
    private ArrayList<String> arrayWords;

    public SentimentPost(String textPost, int sentimentResult) {
        this.sentimentResult = sentimentResult;
        this.textPost = textPost;
    }

    public SentimentPost(String textPost, Integer markOfEmojis) {
        this.textPost = textPost;
        this.markOfEmoticons = markOfEmojis;
    }


    public SentimentPost(String textPost, Integer markOfEmojis, ArrayList<String> arrayWords, boolean strengthening){
        this.textPost = textPost;
        this.markOfEmoticons = markOfEmojis;
        this.arrayWords = arrayWords;
        this.strengthening = strengthening;
    }

    public SentimentPost(String textPost, Integer markOfEmojis, ArrayList<String> arrayWords, Integer postId){
        this.textPost = textPost;
        this.markOfEmoticons = markOfEmojis;
        this.arrayWords = arrayWords;
        this.postId = postId;
    }

    public SentimentPost(String textPost, Integer markOfEmojis, boolean strengthening){
        this.textPost = textPost;
        this.markOfEmoticons = markOfEmojis;
        this.strengthening = strengthening;
    }

    public Integer getMarkOfEmoticons() {
        return markOfEmoticons;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public void setMarkOfEmoticons(Integer markOfEmoticons) {
        this.markOfEmoticons = markOfEmoticons;
    }

    public boolean getStrengthening() {
        return strengthening;
    }

    public void setStrengthening(boolean strengthening) {
        this.strengthening = strengthening;
    }

    public ArrayList<String> getArrayWords() {
        return arrayWords;
    }

    public void setArrayWords(ArrayList<String> arrayWords) {
        this.arrayWords = arrayWords;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getTextPost() {
        return textPost;
    }

    public void setTextPost(String textPost) {
        this.textPost = textPost;
    }

    public int getSentimentResult() {
        return sentimentResult;
    }

    public void setSentimentResult(int sentimentResult) {
        this.sentimentResult = sentimentResult;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentimentPost that = (SentimentPost) o;

        return textPost != null ? textPost.equals(that.textPost) : that.textPost == null;
    }

    @Override
    public int hashCode() {
        return textPost != null ? textPost.hashCode() : 0;
    }
}
