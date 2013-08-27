package com.roger.weibotool.bean;

public class User
{
    private String id;
    private String screen_name;
    private String gender;
    private String profile_image_url;
    private String location;
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getScreen_name()
    {
        return screen_name;
    }
    public void setScreen_name(String screen_name)
    {
        this.screen_name = screen_name;
    }
    public String getGender()
    {
        return gender;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }
    public String getProfile_image_url()
    {
        return profile_image_url;
    }
    public void setProfile_image_url(String profile_image_url)
    {
        this.profile_image_url = profile_image_url;
    }
    public String getLocation()
    {
        return location;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }

}
