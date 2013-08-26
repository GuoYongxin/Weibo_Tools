package com.roger.weibotool.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

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

import com.roger.weibotool.R;
import com.roger.weibotool.constant.ConstantS;
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
				break;
			default:
		}

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
				Log.e(TAG, "shouldOverrideUrlLoading" + url);
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
				Log.e(TAG, "onPageStarted" + url);
				mProgressBar.setVisibility(View.VISIBLE);
				if (url.contains(ConstantS.TEMP_FLAG_CODE)) {
					String code = url.substring(url.indexOf('=') + 1);
					Log.v(TAG, "Code:" + code);
					String accessTokenUrl = getAccessTokenUrl(code);
					Log.v(TAG, "accessTokenUrl ur:l" + accessTokenUrl);
					mWebView.setVisibility(View.INVISIBLE);
					mProgressBar.setVisibility(View.VISIBLE);
					new TokenThread(accessTokenUrl).start();
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
		return ConstantS.AUTH_URL.replace("YOUR_CLIENT_ID", ConstantS.APP_KEY).replace("YOUR_REGISTERED_REDIRECT_URI", ConstantS.REDIRECT_URL);
	}

	private String getAccessTokenUrl(String code) {
		return ConstantS.Token_URL.replace("YOUR_CLIENT_ID", ConstantS.APP_KEY).replace("YOUR_REGISTERED_REDIRECT_URI", ConstantS.REDIRECT_URL)
				.replace("YOUR_CLIENT_SECRET", ConstantS.APP_Secret).replace("CODE", code);
	}

	private void getToken(String url) {
		HttpClient http = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", ConstantS.APP_KEY));
		params.add(new BasicNameValuePair("client_secret", ConstantS.APP_Secret));
		HttpResponse httpResponse;
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			httpResponse = http.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.v(TAG, strResult);
				mHandle.post(new Runnable() {
					@Override
					public void run() {
						AuthenticateActivity.this.finish();
					}
				});
			} else {
				Log.v(TAG, "Failed get token");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class TokenThread extends Thread {
		String url;

		public TokenThread(String url) {
			this.url = url;

		}

		@Override
		public void run() {
			getToken(url);
			super.run();
		}

	}
}
