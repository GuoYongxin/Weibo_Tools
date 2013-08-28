package com.roger.weibotool.bean;

import java.io.Serializable;

public class Tweet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8972849781575461601L;
	private String text;
	private String id;
	private String thumbnail_pic;
	private String repostCount;
	private String created_at;
	private User user;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPic() {
		return thumbnail_pic;
	}

	public void setPic(String tumbnail) {
		this.thumbnail_pic = tumbnail;
	}

	public String getRepostCount() {
		return repostCount;
	}

	public void setRepostCount(String repostCount) {
		this.repostCount = repostCount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return user.toString() + "\t" + id + "\t" + text + "\t" + "\t";
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
}
