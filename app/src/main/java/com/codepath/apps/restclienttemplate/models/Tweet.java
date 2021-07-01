package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.core.text.HtmlCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    private static final String TAG = "ClassTweet";
    public String body;
    public String createdAt;
    public String timeStamp;
    public String source;
    public User user;
    public List<String> imageUrls;
    public int retweetCount;
    public int likeCount;
    public int replyCount;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    // empty constructor needed for Parcel
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.timeStamp = tweet.createdAt;
        tweet.createdAt = tweet.getRelativeTimeAgo(tweet.createdAt);
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.retweetCount = Integer.parseInt(jsonObject.getString("retweet_count"));
        if (!jsonObject.isNull("favorite_count")) {
            tweet.likeCount = Integer.parseInt(jsonObject.getString("favorite_count"));
        } else {
            tweet.likeCount = 0;
        }

        tweet.source = HtmlCompat.fromHtml(jsonObject.getString("source"), HtmlCompat.FROM_HTML_MODE_LEGACY).toString();

        tweet.imageUrls = new ArrayList<>();

        // extract photo urls, if any, from json
        if (jsonObject.has("extended_entities")) {
            JSONObject entities = jsonObject.getJSONObject("extended_entities");

            // entities only has one image url -- may add multiple photos later so imageUrls is an array instead of just one string
            // note that many embeds are simply not going to work this way as it only fetches natively uploaded twitter photos, and even then
            // sometimes these are not counted as embedded uploads (some twitter accounts use t.co manually, which is odd but prevents
            // it from showing up in the API call)
            JSONArray media = entities.getJSONArray("media");
            for (int i = 0; i < media.length(); i++) {
                if (media.getJSONObject(i).getString("type").equals("photo")) {
                    tweet.imageUrls.add(media.getJSONObject(i).getString("media_url_https"));
                }
            }
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}
