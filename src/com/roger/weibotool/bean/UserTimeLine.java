package com.roger.weibotool.bean;

import com.roger.weibotool.common.parser.WeiboParser.IJSONObject;

public class UserTimeLine implements IJSONObject {
	private Tweet[] statuses;
	private int total_number;
	private String next_cursor;
	public Tweet[] getStatuses() {
		return statuses;
	}
	public void setStatuses(Tweet[] statuses) {
		this.statuses = statuses;
	}
	public int getTotal_number() {
		return total_number;
	}
	public void setTotal_number(int total_number) {
		this.total_number = total_number;
	}
	public String getNext_cursor() {
		return next_cursor;
	}
	public void setNext_cursor(String next_cursor) {
		this.next_cursor = next_cursor;
	}
}
