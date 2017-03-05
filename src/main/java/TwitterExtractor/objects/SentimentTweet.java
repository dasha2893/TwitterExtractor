package TwitterExtractor.objects;


public class SentimentTweet {
    private Integer postId;
    private String userName;
    private String textPost;
    private int sentimentResult;
    private Long views;

    public SentimentTweet(String textPost, int sentimentResult) {
        this.sentimentResult = sentimentResult;
        this.textPost = textPost;
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

        SentimentTweet that = (SentimentTweet) o;

        return textPost != null ? textPost.equals(that.textPost) : that.textPost == null;
    }

    @Override
    public int hashCode() {
        return textPost != null ? textPost.hashCode() : 0;
    }
}
