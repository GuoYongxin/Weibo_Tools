package com.roger.weibotool.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.roger.weibotool.R;
import com.roger.weibotool.bean.Oauth2AccessToken;
import com.roger.weibotool.bean.Tweet;
import com.roger.weibotool.common.parser.WeiboParser;
import com.roger.weibotool.constant.ExtrasKey;
import com.roger.weibotool.http.WeiboClient;
import com.roger.weibotool.util.AccessTokenKeeper;

public class ToolsActivity extends Activity implements OnItemClickListener {

	public static final String TAG = "ToolsActivity";
	ListView mListView;
	TextView mTextView;
	ProgressBar mProgressBar;
	Button mButton;
	ImageLoader imageLoader = ImageLoader.getInstance();

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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle bd = new Bundle();
		bd.putLong(ExtrasKey.TWEET_ID, id);
		Intent intent = new Intent();
		intent.putExtras(bd);
		intent.setClass(this, RepostActivity.class);
		startActivity(intent);
	}

	private void getData() {
		String url = "2/statuses/user_timeline.json";
		RequestParams params = new RequestParams();
		Oauth2AccessToken token = AccessTokenKeeper
				.readAccessToken(getApplicationContext());
		params.put("access_token", token.getToken());
		params.put("", token.getUid());
		params.put("count", "30");
		WeiboClient.get(url, params, responseHandler);
		Log.v(TAG, AsyncHttpClient.getUrlWithQueryString(url, params));
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		};
	};
	private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

		@Override
		public void onStart() {
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onFinish() {
			mProgressBar.setVisibility(View.GONE);
		}

		@Override
		public void onSuccess(int statusCode, JSONObject response) {
			Log.v(TAG, "onSuccess");
			ArrayList<Tweet> tweets = WeiboParser.parseTweetArray(response,
					"statuses");
			WeiboAdapter adapter = new WeiboAdapter(tweets);
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(ToolsActivity.this);
		}

		@Override
		public void onFailure(Throwable e, JSONObject errorResponse) {
			Log.v(TAG, "onFailure");
			Toast.makeText(getApplicationContext(), "Fail to retrieve data",
					Toast.LENGTH_SHORT).show();
		}

	};

	private class WeiboAdapter extends BaseAdapter {
		private ArrayList<Tweet> mTweets;

		public WeiboAdapter(ArrayList<Tweet> tweets) {
			mTweets = tweets;
		}

		public void addAll(ArrayList<Tweet> tweets) {
			mTweets.addAll(tweets);
			// notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (mTweets == null)
				return 0;
			return mTweets.size();
		}

		@Override
		public Object getItem(int position) {
			return mTweets.get(position);
		}

		@Override
		public long getItemId(int position) {
			long id = 0;
			try {
				id = Long.parseLong(mTweets.get(position).getId());
			} catch (Exception e) {
				Log.v(TAG, "parse long exception");
			}
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.item_single_weibo, parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Tweet tweet = mTweets.get(position);
			holder.username.setText(tweet.getUser().getScreen_name());
			holder.repostCount.setText("转发：" + tweet.getRepostCount());
			holder.text.setText(tweet.getText());
			if (tweet.getPic() != null) {
				holder.img.setVisibility(View.VISIBLE);
				imageLoader.displayImage(tweet.getPic(), holder.img, options);
			} else {
				holder.img.setVisibility(View.GONE);
			}
			imageLoader.displayImage(tweet.getUser().getProfile_image_url(),
					holder.avatar, options);
			return convertView;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.icon).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	private class ViewHolder {
		ImageView avatar;
		ImageView img;
		TextView username;
		TextView repostCount;
		TextView text;

		public ViewHolder(View view) {
			avatar = (ImageView) view.findViewById(R.id.weibo_avatar);
			img = (ImageView) view.findViewById(R.id.weibo_pic);
			username = (TextView) view.findViewById(R.id.weibo_username);
			repostCount = (TextView) view.findViewById(R.id.weibo_retweet_num);
			text = (TextView) view.findViewById(R.id.weibo_content);
		}
	}

}
