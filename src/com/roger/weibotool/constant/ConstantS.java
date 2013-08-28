package com.roger.weibotool.constant;

public interface ConstantS {

	public static final String APP_KEY = "3038407034";

	public static final String APP_Secret = "9b5f357d147d43dfdee506166e9ba5af";

	public static final String BASE_URL = "https://api.weibo.com/";

	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	public static final String AUTH_URL = "oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&display=mobile";

	public static final String Token_URL = "oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE";

	public static final String TEMP_FLAG_CODE = "?code=";

}
