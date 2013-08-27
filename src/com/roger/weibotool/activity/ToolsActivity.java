package com.roger.weibotool.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.roger.weibotool.R;
import com.roger.weibotool.bean.Oauth2AccessToken;
import com.roger.weibotool.bean.Tweet;
import com.roger.weibotool.bean.User;
import com.roger.weibotool.http.WeiboClient;
import com.roger.weibotool.util.AccessTokenKeeper;


public class ToolsActivity extends Activity
{

    public static final String TAG = "ToolsActivity";
    ListView mListView;
    TextView mTextView;
    ProgressBar mProgressBar;
    Button mButton;
    ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        mListView = (ListView) findViewById(R.id.tool_listview);
        mTextView = (TextView) findViewById(R.id.tool_username);
        mProgressBar = (ProgressBar) findViewById(R.id.tool_progressbar);
        mButton = (Button) findViewById(R.id.tool_more_btn);
        getData();
    }

    private void getData()
    {
        String url = "2/statuses/user_timeline.json";
        RequestParams params = new RequestParams();
        Oauth2AccessToken token = AccessTokenKeeper
                .readAccessToken(getApplicationContext());
        params.put("access_token", token.getToken());
        params.put("", token.getUid());
        params.put("count", "30");
        WeiboClient.get(url, params, responseHandler);
    }

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
        };
    };
    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler()
    {

        @Override
        public void onSuccess(int statusCode, JSONObject response)
        {
            Log.v(TAG, "onSuccess");
            ArrayList<Tweet> tweets = parseTweet(response);
            WeiboAdapter adapter = new WeiboAdapter(tweets);
            mListView.setAdapter(adapter);
        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse)
        {
            Log.v(TAG, "onFailure");
            Toast.makeText(getApplicationContext(), "Fail to retrieve data",
                    Toast.LENGTH_SHORT).show();
        }

    };

    private ArrayList<Tweet> parseTweet(JSONObject response)
    {
        ArrayList<Tweet> list = new ArrayList<Tweet>();
        try
        {
            JSONArray statusesArray = response.getJSONArray("statuses");
            Log.v(TAG, "length:" + statusesArray.length());
            System.out.println(statusesArray.toString());
            for (int i = 0; i < statusesArray.length(); i++)
            {
                Tweet tweet = new Tweet();
                JSONObject tweetObj = statusesArray.getJSONObject(i);
                tweet.setId(tweetObj.getString("id"));
                tweet.setText(tweetObj.getString("text"));
                if (tweetObj.has("thumbnail_pic"))
                    tweet.setTumbnail(tweetObj.getString("thumbnail_pic"));
                tweet.setRepostCount(tweetObj.getString("reposts_count"));
                User user = new User();
                JSONObject userObj = tweetObj.getJSONObject("user");
                user.setId(userObj.getString("id"));
                user.setScreen_name(userObj.getString("screen_name"));
                user.setLocation(userObj.getString("location"));
                user.setProfile_image_url(userObj
                        .getString("profile_image_url"));

                tweet.setUser(user);
                list.add(tweet);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    private class WeiboAdapter extends BaseAdapter
    {
        private ArrayList<Tweet> mTweets;

        public WeiboAdapter(ArrayList<Tweet> tweets)
        {
            mTweets = tweets;
        }

        @Override
        public int getCount()
        {
            if (mTweets == null)
                return 0;
            return mTweets.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mTweets.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_single_weibo, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            Tweet tweet = mTweets.get(position);
            holder.username.setText(tweet.getUser().getScreen_name());
            holder.repostCount.setText("转发：" + tweet.getRepostCount());
            holder.text.setText(tweet.getText());
            try
            {
                imageLoader.displayImage(tweet.getTumbnail(), holder.img);
                imageLoader.displayImage(
                        tweet.getUser().getProfile_image_url(), holder.avatar);
            }
            catch (Exception e)
            {

            }
            return convertView;
        }

    }

    private class ViewHolder
    {
        ImageView avatar;
        ImageView img;
        TextView username;
        TextView repostCount;
        TextView text;

        public ViewHolder(View view)
        {
            avatar = (ImageView) view.findViewById(R.id.weibo_avatar);
            img = (ImageView) view.findViewById(R.id.weibo_pic);
            username = (TextView) view.findViewById(R.id.weibo_username);
            repostCount = (TextView) view.findViewById(R.id.weibo_retweet_num);
            text = (TextView) view.findViewById(R.id.weibo_content);
        }
    }
}
