package com.roger.weibotool;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WeiboApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		ImageLoaderConfiguration config =  ImageLoaderConfiguration.createDefault(getApplicationContext());
		ImageLoader.getInstance().init(config);
	}

}
