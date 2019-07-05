package com.codepath.apps.TwitterClone.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.TwitterClone.R;
import com.codepath.apps.TwitterClone.TwitterApplication;
import com.codepath.apps.TwitterClone.TwitterClient;
import com.codepath.apps.TwitterClone.adapters.TweetAdapter;
import com.codepath.apps.TwitterClone.fragments.ComposeFragment;
import com.codepath.apps.TwitterClone.models.Tweet;
import com.codepath.apps.TwitterClone.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeDialogListener  {
    private final int REQUEST_CODE = 20;

    long smallestTweetId = Long.MAX_VALUE;

    @BindView(R.id.rvTweet) RecyclerView rvTweets;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.compose_tweet) FloatingActionButton fabComposeTweet;

    LinearLayoutManager linearLayoutManager;
    EndlessRecyclerViewScrollListener scrollListener;

    TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient(this);

        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);

        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        setupSwipeContainer();
        setupScroll();
        populateTimeline(0, true);

        fabComposeTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComposeTweetClick();
            }
        });
    }

    @Override
    public void onSavedTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    private void populateTimeline(long maxId, final boolean resetList) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (resetList) {
                    tweetAdapter.clear();
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);

                        if (tweet.uid < smallestTweetId) {
                            smallestTweetId = tweet.uid;
                        }

                        tweetAdapter.notifyItemChanged(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Error loading timeline", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
            }
        }, maxId);
    }

    private void onComposeTweetClick() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment editNameDialogFragment = ComposeFragment.newInstance();
        editNameDialogFragment.show(fm, "fragment_compose");
    }

    private void setupSwipeContainer() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(0, true);
            }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupScroll() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(smallestTweetId - 1, false);
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
    }
}
