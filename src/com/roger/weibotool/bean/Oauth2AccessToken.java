package com.roger.weibotool.bean;

import java.io.Serializable;

public class Oauth2AccessToken implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 734972545851327048L;
	private String token;
	private long expireTime;
	private String uid;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AccessToken:" + getToken() + "\n" + "Expire in:" + getExpireTime() + "\n" + "Uid:" + getUid();
	}
}
