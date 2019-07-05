package com.codepath.apps.TwitterClone.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.TwitterClone.R;
import com.codepath.apps.TwitterClone.TwitterApplication;
import com.codepath.apps.TwitterClone.TwitterClient;
import com.codepath.apps.TwitterClone.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ComposeFragment extends DialogFragment {
    @BindView(R.id.etComposeTweet) EditText etComposeTweet;

    ProgressDialog pd;
    TwitterClient client;

    public interface ComposeDialogListener {
        void onSavedTweet(Tweet tweet);
    }

    public ComposeFragment() {}

    public static ComposeFragment newInstance() {
        ComposeFragment frag = new ComposeFragment();

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        client = TwitterApplication.getRestClient(getContext());

        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setupProgressDialog();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("New tweet");

        alertDialogBuilder.setPositiveButton("Tweet!",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onComposeTweet();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_compose, null);

        alertDialogBuilder.setView(view);

        ButterKnife.bind(this, view);
        etComposeTweet.requestFocus();

        return alertDialogBuilder.create();
    }

    private void setupProgressDialog() {
        pd = new ProgressDialog(getContext());
        pd.setTitle("Sending tweet...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
    }

    private void onComposeTweet() {
        String status = etComposeTweet.getText().toString();

        if (status.equals("")) {
            Toast.makeText(getContext(), "Tweet cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        pd.show();
        sendComposeTweetRequest(status);
    }

    private void sendComposeTweetRequest(String status) {
        final ComposeDialogListener listener = (ComposeDialogListener) getActivity();

        client.sendTweet(status, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    listener.onSavedTweet(tweet);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error during parsing! Try again...", Toast.LENGTH_SHORT).show();
                }

                pd.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                pd.dismiss();
                Toast.makeText(getContext(), "Error! Try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
