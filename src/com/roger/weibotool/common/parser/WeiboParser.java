package com.roger.weibotool.common.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.roger.weibotool.bean.Tweet;
import com.roger.weibotool.bean.User;

public class WeiboParser {
	public static final String TAG = "WeiboParser";

	public static ArrayList<Tweet> parseTweetArray(JSONObject response,String tag) {
		ArrayList<Tweet> list = new ArrayList<Tweet>();
		try {
			JSONArray statusesArray = response.getJSONArray(tag);
			Log.v(TAG, "length:" + statusesArray.length());
			for (int i = 0; i < statusesArray.length(); i++) {
				Tweet tweet = new Tweet();
				JSONObject tweetObj = statusesArray.getJSONObject(i);
				tweet = parseTweet(tweetObj);
				JSONObject userObj = tweetObj.getJSONObject("user");
				User user = parseUser(userObj);
				tweet.setUser(user);
				list.add(tweet);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static Tweet parseTweet(JSONObject tweetObj) {
		Tweet tweet = new Tweet();
		try {
			tweet.setId(tweetObj.getString("id"));
			tweet.setText(tweetObj.getString("text"));
			tweet.setCreated_at(tweetObj.getString("created_at"));
			if (tweetObj.has("thumbnail_pic")) tweet.setPic(tweetObj.getString("thumbnail_pic"));
			tweet.setRepostCount(tweetObj.getString("reposts_count"));
		} catch (JSONException e) {
			Log.v(TAG, "parse tweet exception");
			e.printStackTrace();
		}
		return tweet;
	}

	private static User parseUser(JSONObject userObj) {
		User user = new User();
		try {
			user.setId(userObj.getString("id"));
			user.setScreen_name(userObj.getString("screen_name"));
			user.setLocation(userObj.getString("location"));
			user.setProfile_image_url(userObj.getString("profile_image_url"));
			user.setGender(userObj.getString("gender"));
		} catch (JSONException e) {
			Log.v(TAG, "parse user exception");
			e.printStackTrace();
		}
		return user;
	}
}
