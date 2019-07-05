package com.codepath.apps.TwitterClone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.TwitterClone.R;
import com.codepath.apps.TwitterClone.TwitterApplication;
import com.codepath.apps.TwitterClone.TwitterClient;
import com.codepath.apps.TwitterClone.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    @BindView(R.id.etComposeTweet) EditText etComposeTweet;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient(this);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }

    public void onComposeTweet(View view) {
        String status = etComposeTweet.getText().toString();

        if (status.equals("")) {
            Toast.makeText(this, "Tweet cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        sendComposeTweetRequest(status);
    }

    public void sendComposeTweetRequest(String status) {
        client.sendTweet(status, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);

                    Intent data = new Intent();
                    data.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));

                    setResult(RESULT_OK, data);

                    finish();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error during parsing! Try again...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Error! Try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
