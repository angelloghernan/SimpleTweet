package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.w3c.dom.Text;

import java.util.Locale;

import okhttp3.Headers;

// This class is needed only so that we do not have to write the same code every single time
// for every like, retweet, and reply action (ie details activity buttons and timeline buttons).
// This will be extra useful is we decide to show full threads later on.
public class TweetInteractions implements View.OnClickListener {
    ImageButton ibRetweetButton;
    ImageButton ibLikeButton;
    ImageButton ibReplyButton;
    TwitterClient client;
    Context context;
    Tweet tweet;
    TextView likeCounter;
    TextView retweetCounter;

    public static final String TAG = "TweetInteractions";

    public TweetInteractions(Context context, ImageButton likeButton, ImageButton retweetButton, ImageButton replyButton,
                            TwitterClient client, Tweet tweet){
        this.client = client;
        this.ibRetweetButton = retweetButton;
        this.ibLikeButton = likeButton;
        this.ibReplyButton = replyButton;
        this.context = context;
        this.tweet = tweet;
        this.likeCounter = likeCounter;
        this.retweetCounter = retweetCounter;
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
                        Log.e(TAG, response);
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
                    Log.e(TAG, response);
                    Log.e(TAG, "onFailure to like tweet: " + throwable.toString());
                }
            });
        }
        else if (v.getId() == R.id.ibRetweet || v.getId() == R.id.ibDetailsRetweet) {
            if (tweet.retweeted) {
                client.unretweetTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "unretweet successful");
                        ibRetweetButton.setSelected(false);
                        tweet.retweetCount -= 1;
                        tweet.retweeted = false;
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "error unretweeting: " + throwable.toString());
                        Log.e(TAG, response);
                    }
                });
            } else {
                client.retweetTweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "retweet successful");
                        ibRetweetButton.setSelected(true);
                        tweet.retweetCount += 1;
                        tweet.retweeted = true;
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "error retweeting: " + throwable.toString());
                        Log.e(TAG, response);
                    }
                });
            }
        }
    }

}
