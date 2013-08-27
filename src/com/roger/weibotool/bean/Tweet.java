package com.roger.weibotool.bean;

public class Tweet
{

    private String text;
    private String id;
    private String tumbnail;
    private String repostCount;

    private User user;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTumbnail()
    {
        return tumbnail;
    }

    public void setTumbnail(String tumbnail)
    {
        this.tumbnail = tumbnail;
    }

    public String getRepostCount()
    {
        return repostCount;
    }

    public void setRepostCount(String repostCount)
    {
        this.repostCount = repostCount;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }
}
