package com.roger.weibotool.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roger.weibotool.R;
import com.roger.weibotool.bean.Oauth2AccessToken;
import com.roger.weibotool.http.WeiboClient;
import com.roger.weibotool.util.AccessTokenKeeper;

public class ToolsActivity extends Activity {

	public static final String TAG = "ToolsActivity";
	ListView mListView;
	TextView mTextView;
	ProgressBar mProgressBar;
	Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools);
		mListView = (ListView) findViewById(R.id.tool_listview);
		mTextView = (TextView) findViewById(R.id.tool_username);
		mProgressBar = (ProgressBar) findViewById(R.id.tool_progressbar);
		mButton = (Button) findViewById(R.id.tool_more_btn);
		getData();
	}

	private void getData() {
		String url = "2/statuses/user_timeline.json";
		RequestParams params = new RequestParams();
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
		params.put("access_token", token.getToken());
		params.put("", token.getUid());
		params.put("count", "30");
		WeiboClient.get(url, params, responseHandler);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		};
	};
	private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, JSONObject response) {
			Log.v(TAG, "onSuccess");
			try {
				JSONArray statusesArray = response.getJSONArray("statuses");
				Log.v(TAG, "length:" + statusesArray.length());
				System.out.println(statusesArray.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable e, JSONObject errorResponse) {
			Log.v(TAG, "onFailure");
			Toast.makeText(getApplicationContext(), "Fail to retrieve data", Toast.LENGTH_SHORT).show();
		}

	};

	private class WeiboAdapter extends BaseAdapter {

		public WeiboAdapter() {

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
