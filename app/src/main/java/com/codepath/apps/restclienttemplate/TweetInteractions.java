package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.Locale;

import okhttp3.Headers;

public class TweetInteractions implements View.OnClickListener {
    ImageButton ibRetweetButton;
    ImageButton ibLikeButton;
    ImageButton ibReplyButton;
    TwitterClient client;
    Context context;
    Tweet tweet;

    public static final String TAG = "TweetInteractions";

    public TweetInteractions(Context context, ImageButton likeButton, ImageButton retweetButton, ImageButton replyButton,
                            TwitterClient client, Tweet tweet){
        this.client = client;
        this.ibRetweetButton = retweetButton;
        this.ibLikeButton = likeButton;
        this.ibReplyButton = replyButton;
        this.context = context;
        this.tweet = tweet;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibLike || v.getId() == R.id.ibDetailsLike) {
            if (tweet.liked) {
                client.unlikeTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Tweet unlike successful");
                        ibLikeButton.setSelected(false);
                        tweet.likeCount -= 1;
                        tweet.liked = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, response.toString());
                        Log.e(TAG, "onFailure to like tweet: " + throwable.toString());
                    }
                });
                return;
            }
            client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "Tweet like successful");
                    ibLikeButton.setSelected(true);
                    tweet.likeCount += 1;
                    tweet.liked = true;
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, response.toString());
                    Log.e(TAG, "onFailure to like tweet: " + throwable.toString());
                }
            });
        }
    }

    public void updateLikeCount(TextView likeCountView, int likeCount) {
        likeCountView.setText(String.format(Locale.ENGLISH, "%d", likeCount));
    }
}
