package com.codepath.apps.TwitterClone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codepath.apps.TwitterClone.R;
import com.codepath.apps.TwitterClone.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvUserNameAndUsername) TextView tvUserNameAndUsername;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.toolbar) Toolbar toolbar;

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        setTextViewsValues();
    }

    private void setTextViewsValues() {
        tvBody.setText(tweet.body);
        tvUserNameAndUsername.setText(tweet.user.name + " (@" + tweet.user.screenName + ")");
        tvCreatedAt.setText(tweet.getRelativeTimeAgo());
        tvFavoriteCount.setText("Favorites: " + tweet.favoriteCount);
        tvRetweetCount.setText("Retweets: " + tweet.retweetCount);
    }
}
