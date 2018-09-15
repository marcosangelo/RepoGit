package br.com.repogit.Model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PullRequest {
    private String pullRequest;
    private String descPullRequest;
    private String user;
    private Date dtCreatedAt;
    private String urlImage;
    private String htmlUrl;
    private boolean isOpen;



    public PullRequest(String pullRequest, String descPullRequest, String htmlUrl, String user, String urlImage, String dtCreatedAt, String state) {

        this.pullRequest = pullRequest;
        this.descPullRequest = descPullRequest;
        this.user = user;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date data = sdf.parse(dtCreatedAt);
            this.dtCreatedAt = data;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.urlImage = urlImage;
        this.htmlUrl = htmlUrl;

        this.isOpen = false;
        if (state.contains("open")){
            this.isOpen = true;
        }
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDtCreatedAt() {
        return dtCreatedAt;
    }

    public void setDtCreatedAt(Date dtCreatedAt) {
        this.dtCreatedAt = dtCreatedAt;
    }

    public String getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(String pullRequest) {
        this.pullRequest = pullRequest;
    }

    public String getDescPullRequest() {
        return descPullRequest;
    }

    public void setDescPullRequest(String descPullRequest) {
        this.descPullRequest = descPullRequest;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }
}
