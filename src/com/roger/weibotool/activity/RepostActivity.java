package com.roger.weibotool.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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


public class RepostActivity extends Activity implements OnClickListener
{
    public static final String TAG = "RepostActivity";
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private long tweet_id;
    private long next_max_id;
    private TextView mTitleText;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private Button mMore;
    private Button mAll;
    private Button mSend;
    private RepostAdapter mAdapter = new RepostAdapter(null);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repost_detail);
        mTitleText = (TextView) findViewById(R.id.repost_title);
        mListView = (ListView) findViewById(R.id.repost_listview);
        mProgressBar = (ProgressBar) findViewById(R.id.repost_progressbar);
        mMore = (Button) findViewById(R.id.repost_more_btn);
        mAll = (Button) findViewById(R.id.repost_all_btn);
        mSend = (Button) findViewById(R.id.repost_send_btn);
        readExtras();
        initComponents();
        getData();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.repost_more_btn:
            getData();
            break;
        case R.id.repost_all_btn:
            getData(200);
            break;
        case R.id.repost_send_btn:
            sendData();
            break;
        default:
            break;
        }
    }

    private void initComponents()
    {
        mMore.setOnClickListener(this);
        mAll.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    private void readExtras()
    {
        Bundle bd = getIntent().getExtras();
        if (bd != null)
        {
            tweet_id = (Long) bd.get(ExtrasKey.TWEET_ID);
            Log.v(TAG, "Read Extras:" + tweet_id);
        }
    }

    private void getData()
    {
        getData(20);
    }

    private void getData(int count)
    {
        String url = "2/statuses/repost_timeline.json";
        RequestParams params = new RequestParams();
        Oauth2AccessToken token = AccessTokenKeeper
                .readAccessToken(getApplicationContext());
        params.put("access_token", token.getToken());
         params.put("id", String.valueOf(tweet_id));
        // FIXME hard code ID
//        params.put("id", "3614237831872267");
        params.put("count", String.valueOf(count));
        if (next_max_id != 0l)
            params.put("max_id", String.valueOf(next_max_id - 1l));
        WeiboClient.get(url, params, responseHandler);
        Log.v(TAG, AsyncHttpClient.getUrlWithQueryString(url, params));
    }

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler()
    {

        @Override
        public void onStart()
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish()
        {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(int statusCode, JSONObject response)
        {
            Log.v(TAG, "onSuccess");
            ArrayList<Tweet> tweets = WeiboParser.parseTweetArray(response,
                    "reposts");
            if (tweets != null && tweets.size() > 0)
            {
                mAdapter.addAll(tweets);
                next_max_id = Long.valueOf(tweets.get(tweets.size() - 1)
                        .getId());
                mTitleText.setText("共"+mAdapter.getCount()+"条转发");
                mAdapter.notifyDataSetChanged();
                Log.v(TAG, "next_max_id:" + next_max_id);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No more Data",
                        Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse)
        {
            Log.v(TAG, "onFailure");
            Toast.makeText(getApplicationContext(), "Fail to retrieve data",
                    Toast.LENGTH_SHORT).show();
        }

    };

    private void sendData()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        StringBuilder content = new StringBuilder();
        ArrayList<Tweet> list = mAdapter.getmTweets();
        for (int i = 0; i < list.size(); i++)
        {
            content.append(list.get(i).toString());
        }
        System.out.println(content.toString());
        File f = writeFile(content.toString());
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Weibo Repost Details");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        sendIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        sendIntent.setType("text/html");

        // System.out.println("File:" + f);

        // sendIntent.setType("application/octet-stream");
        startActivity(sendIntent);
    }

    private File writeFile(String content)
    {
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/data/temp");
        dir.mkdirs();
        File f = new File(dir, "Data.csv");

        try
        {
            f.createNewFile();
            // f = File.createTempFile("test.csv", null, getApplicationContext()
            // .getCacheDir());
            FileOutputStream fo = new FileOutputStream(f);
            OutputStreamWriter write = new OutputStreamWriter(fo, "GBK");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(content);
            writer.close();
            // fo.write(content.getBytes());
            // fo.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return f;
    }

    private class RepostAdapter extends BaseAdapter
    {
        private ArrayList<Tweet> mTweets = new ArrayList<Tweet>(20);

        public RepostAdapter(ArrayList<Tweet> tweets)
        {
            addAll(tweets);
        }

        public void addAll(ArrayList<Tweet> tweets)
        {
            if (tweets != null)
                getmTweets().addAll(tweets);
        }

        @Override
        public int getCount()
        {
            if (getmTweets() == null)
                return 0;
            return getmTweets().size();
        }

        @Override
        public Object getItem(int position)
        {
            return getmTweets().get(position);
        }

        @Override
        public long getItemId(int position)
        {
            long id = 0;
            try
            {
                id = Long.parseLong(getmTweets().get(position).getId());
            }
            catch (Exception e)
            {
                Log.v(TAG, "parse long exception");
            }
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_repost, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            Tweet tweet = getmTweets().get(position);
            holder.username.setText(tweet.getUser().getScreen_name());
            holder.gender.setText(tweet.getUser().getGender());
            holder.location.setText(tweet.getUser().getLocation());
            holder.time.setText(tweet.getCreated_at());
            holder.text.setText(tweet.getText());
            imageLoader.displayImage(tweet.getUser().getProfile_image_url(),
                    holder.avatar, options);
            return convertView;
        }

        public ArrayList<Tweet> getmTweets()
        {
            return mTweets;
        }

        public void setmTweets(ArrayList<Tweet> mTweets)
        {
            this.mTweets = mTweets;
        }
    }

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.icon).cacheInMemory(true)
            .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    private class ViewHolder
    {
        ImageView avatar;
        TextView username;
        TextView gender;
        TextView location;
        TextView text;
        TextView time;

        public ViewHolder(View view)
        {
            avatar = (ImageView) view.findViewById(R.id.repost_avatar);
            username = (TextView) view.findViewById(R.id.repost_username);
            gender = (TextView) view.findViewById(R.id.repost_gender);
            location = (TextView) view.findViewById(R.id.repost_location);
            time = (TextView) view.findViewById(R.id.repost_retweet_time);
            text = (TextView) view.findViewById(R.id.repost_content);
        }
    }

}
