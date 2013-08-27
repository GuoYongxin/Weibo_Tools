package com.roger.weibotool.activity;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roger.weibotool.R;
import com.roger.weibotool.bean.Oauth2AccessToken;
import com.roger.weibotool.constant.ConstantS;
import com.roger.weibotool.http.WeiboClient;
import com.roger.weibotool.util.AccessTokenKeeper;

public class AuthenticateActivity extends Activity implements OnClickListener {
	private Button mLogin;
	private Button mDelete;
	private Button mDetail;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private static final String TAG = "AuthenticateActivity";

	private Handler mHandle = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate);
		mLogin = (Button) findViewById(R.id.auth_oauth_login);
		mDelete = (Button) findViewById(R.id.auth_delete_oauth);
		mDetail = (Button) findViewById(R.id.auth_view_oauth);
		mWebView = (WebView) findViewById(R.id.auth_webview);
		mProgressBar = (ProgressBar) findViewById(R.id.auth_progressbar);
		mLogin.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mDetail.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.auth_oauth_login:
				showLogin();
				break;
			case R.id.auth_delete_oauth:
				deleteToken();
				break;
			case R.id.auth_view_oauth:
				viewToken();
				break;
			default:
		}

	}

	private void viewToken() {
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
		Toast.makeText(getApplicationContext(), "AccessToken:" + token.getToken() + "\n" + "Expire in:" + token.getExpireTime(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBackPressed() {
		if (mWebView.getVisibility() == View.VISIBLE) {
			mWebView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	private void showLogin() {
		String token = AccessTokenKeeper.readAccessToken(this).getToken();
		if (!StringUtils.isEmpty(token)) {
			Toast.makeText(this, "You have already login...", Toast.LENGTH_SHORT).show();
			return;
		}
		mWebView.setVisibility(View.VISIBLE);
		String url = getAuthUrl();
		Log.v(TAG + "raw url:", url);
		mWebView.clearCache(true);
		mWebView.loadUrl(url);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.v(TAG, "shouldOverrideUrlLoading" + url);
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				Log.e(TAG, error.toString());
				handler.proceed();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.v(TAG, "onPageStarted" + url);
				mProgressBar.setVisibility(View.VISIBLE);
				if (url.contains(ConstantS.TEMP_FLAG_CODE)) {
					String code = url.substring(url.indexOf('=') + 1);
					Log.v(TAG, "Code:" + code);
					String accessTokenUrl = getAccessTokenUrl(code);
					Log.v(TAG, "accessTokenUrl ur:l" + accessTokenUrl);
					mWebView.setVisibility(View.INVISIBLE);
					mProgressBar.setVisibility(View.VISIBLE);
//					new TokenThread(accessTokenUrl).start();
					getAccessToken(accessTokenUrl);
				} else {

				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mProgressBar.setVisibility(View.GONE);

			}

		});
	}

	private void deleteToken() {
		AccessTokenKeeper.clear(this);
		Toast.makeText(this, "Token is deleted...", Toast.LENGTH_SHORT).show();
	}

	private String getAuthUrl() {
		return ConstantS.BASE_URL
				+ ConstantS.AUTH_URL.replace("YOUR_CLIENT_ID", ConstantS.APP_KEY).replace("YOUR_REGISTERED_REDIRECT_URI", ConstantS.REDIRECT_URL);
	}

	private String getAccessTokenUrl(String code) {
		return ConstantS.Token_URL.replace("YOUR_CLIENT_ID", ConstantS.APP_KEY).replace("YOUR_REGISTERED_REDIRECT_URI", ConstantS.REDIRECT_URL)
				.replace("YOUR_CLIENT_SECRET", ConstantS.APP_Secret).replace("CODE", code);
	}

	private void getAccessToken(String url) {
		RequestParams params = new RequestParams();
		params.put("client_id", ConstantS.APP_KEY);
		params.put("client_secret", ConstantS.APP_Secret);
		AsyncHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(final JSONObject response) {
//				super.onSuccess(response);
				Log.v(TAG, "Success");
				try {
					String accessToken = response.getString("access_token");
					String expires_in = response.getString("expires_in");
					String remind_in = response.getString("remind_in");
					String uid = response.getString("uid");
					Oauth2AccessToken token = new Oauth2AccessToken();
					token.setToken(accessToken);
					token.setExpireTime(Long.valueOf(expires_in));
					AccessTokenKeeper.keepAccessToken(getApplicationContext(), token);
					mHandle.post(new Runnable() {
						@Override
						public void run() {
							Log.v(TAG, response.toString());
							Toast.makeText(getApplicationContext(), "Success Login", Toast.LENGTH_SHORT).show();
//							AuthenticateActivity.this.finish();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
//				super.onFailure(e, errorResponse);
				Log.v(TAG, "Failure");
				mHandle.post(new Runnable() {
					@Override
					public void run() {

						Toast.makeText(getApplicationContext(), "Failed get token", Toast.LENGTH_SHORT).show();
//						AuthenticateActivity.this.finish();
					}
				});
			}

		};
		Log.v(TAG, "post  " + url);
		WeiboClient.post(url, params, responseHandler);
	}
//	private void getToken(String url) {
//		HttpClient http = new DefaultHttpClient();
//		HttpPost post = new HttpPost(url);
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("client_id", ConstantS.APP_KEY));
//		params.add(new BasicNameValuePair("client_secret", ConstantS.APP_Secret));
//		HttpResponse httpResponse;
//		try {
//			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		try {
//			httpResponse = http.execute(post);
//			if (httpResponse.getStatusLine().getStatusCode() == 200) {
//				String strResult = EntityUtils.toString(httpResponse.getEntity());
//				Log.v(TAG, strResult);
//				mHandle.post(new Runnable() {
//					@Override
//					public void run() {
//						Toast.makeText(getApplicationContext(), "Success Login", Toast.LENGTH_SHORT).show();
//						AuthenticateActivity.this.finish();
//					}
//				});
//			} else {
//				Log.v(TAG, "Failed get token");
//			}
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private class TokenThread extends Thread {
//		String url;
//
//		public TokenThread(String url) {
//			this.url = url;
//
//		}
//
//		@Override
//		public void run() {
//			getToken(url);
//			super.run();
//		}
//
//	}
}
