package TwitterExtractor.objects;


import java.io.Serializable;

public class Account implements Serializable{
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public Account( String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.accessToken = accessToken;
        this.consumerSecret = consumerSecret;
        this.consumerKey = consumerKey;
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (consumerKey != null ? !consumerKey.equals(account.consumerKey) : account.consumerKey != null) return false;
        if (consumerSecret != null ? !consumerSecret.equals(account.consumerSecret) : account.consumerSecret != null)
            return false;
        if (accessToken != null ? !accessToken.equals(account.accessToken) : account.accessToken != null) return false;
        return accessTokenSecret != null ? accessTokenSecret.equals(account.accessTokenSecret) : account.accessTokenSecret == null;

    }

    @Override
    public int hashCode() {
        int result = consumerKey != null ? consumerKey.hashCode() : 0;
        result = 31 * result + (consumerSecret != null ? consumerSecret.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (accessTokenSecret != null ? accessTokenSecret.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenSecret='" + accessTokenSecret + '\'' +
                '}';
    }
}
