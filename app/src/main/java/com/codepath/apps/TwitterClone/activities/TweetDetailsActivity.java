package com.codepath.apps.TwitterClone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.TwitterClone.R;
import com.codepath.apps.TwitterClone.TwitterApplication;
import com.codepath.apps.TwitterClone.TwitterClient;
import com.codepath.apps.TwitterClone.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvUserNameAndUsername) TextView tvUserNameAndUsername;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btLike) Button btLike;
    @BindView(R.id.btRetweet) Button btRetweet;

    Tweet tweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tweet details");

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        client = TwitterApplication.getRestClient(this);

        setTextViewsValues();
        setButtonsText();
    }

    private void setTextViewsValues() {
        tvBody.setText(tweet.body);
        tvUserNameAndUsername.setText(tweet.user.name + " (@" + tweet.user.screenName + ")");
        tvCreatedAt.setText(tweet.getRelativeTimeAgo());
        setFavoriteCountText(tweet.favoriteCount);
        setRetweetCountText(tweet.retweetCount);
    }

    private void setFavoriteCountText(int count) {
        tvFavoriteCount.setText("Favorites: " + count);
    }

    private void setRetweetCountText(int count) {
        tvRetweetCount.setText("Retweets: " + count);
    }

    private void setButtonsText() {
        if (tweet.favorited) {
            btLike.setText("Unlike");
        } else {
            btLike.setText("Like");
        }

         if (tweet.retweeted) {
            btRetweet.setText("Unretweet");
        } else {
            btRetweet.setText("Retweet");
        }
    }

    @OnClick(R.id.btLike)
    public void likeUnlikeTweet(Button button) {
        if (tweet.favorited) {
            unlikeTweet();
        } else {
            likeTweet();
        }
    }

    @OnClick(R.id.btRetweet)
    public void retweetUnretweet(Button button) {
        if (tweet.retweeted) {
            unretweet();
        } else {
            retweet();
        }
    }

    private void likeTweet() {
        client.likeTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                btLike.setText("Unlike");
                tweet.favorited = true;

                tweet.favoriteCount = tweet.favoriteCount + 1;
                setFavoriteCountText(tweet.favoriteCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Couldn't like tweet. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

     private void unlikeTweet() {
        client.unlikeTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                btLike.setText("Like");
                tweet.favorited = false;

                tweet.favoriteCount = tweet.favoriteCount - 1;
                setFavoriteCountText(tweet.favoriteCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Couldn't unlike tweet. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retweet() {
        client.retweetTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                btRetweet.setText("Unretweet");
                tweet.retweeted = false;

                tweet.retweetCount = tweet.retweetCount + 1;
                setRetweetCountText(tweet.retweetCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Couldn't retweet tweet. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unretweet() {
        client.unretweetTweet(tweet.uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                btRetweet.setText("Retweet");
                tweet.retweeted = false;

                tweet.retweetCount = tweet.retweetCount - 1;
                setRetweetCountText(tweet.retweetCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Couldn't unretweet tweet. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
