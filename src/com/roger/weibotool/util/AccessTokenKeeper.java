package com.roger.weibotool.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.roger.weibotool.bean.Oauth2AccessToken;

public class AccessTokenKeeper {
	private static final String PREFERENCES_NAME = "com.roger.weibo";

	public static void keepAccessToken(Context context, Oauth2AccessToken token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getToken());
		editor.putLong("expiresTime", token.getExpireTime());
		editor.putString("uid", token.getUid());
		editor.commit();
	}

	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	public static Oauth2AccessToken readAccessToken(Context context) {
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setToken(pref.getString("token", ""));
		token.setExpireTime(pref.getLong("expiresTime", 0));
		token.setUid(pref.getString("uid", ""));
		return token;
	}
}
