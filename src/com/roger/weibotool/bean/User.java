package com.roger.weibotool.bean;

import java.io.Serializable;
import java.util.Locale;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2017954571284969904L;
	private String id;
	private String screen_name;
	private String gender;
	private String profile_image_url;
	private String location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		if (gender.equals("m")) {
			this.gender = "Male";
		} else {
			this.gender = "Female";
		}
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		// return String.format(Locale.ENGLISH,"%-20s %-20s %-20s %-20s",
		// id,screen_name,gender,location);
		return id + "," + screen_name + "," + gender + "," + location;
		// return id + "\t" + screen_name + "\t" + gender + "\t" + location;
	}
}
