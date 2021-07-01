package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    public static final int REQUEST_CODE = 20;
    public static final int DETAILS_REQUEST_CODE = 30;

    private SwipeRefreshLayout srlTimelineRefresh;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        srlTimelineRefresh = findViewById(R.id.srlTimelineRefresh);

        srlTimelineRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineasync(0);
            }
        });

        srlTimelineRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient(this);

        // Find recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Init list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets, client, callback);
        // Set up recycler view: layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else if (item.getItemId() == R.id.logout) {
            onLogoutClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    // When adapter is clicked, call this so we can start the details activity and get a result.
    // Needed so that we can tell if the tweet was liked/unliked, retweeted/unretweeted in the details activity
    TweetsAdapter.AdapterCallback callback = new TweetsAdapter.AdapterCallback() {
        @Override
        public void onAdapterSelected(int pos, Tweet tweet) {
            Intent intent = new Intent(getApplicationContext(),
                    TweetDetailsActivity.class);
            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            intent.putExtra("position", pos);
            startActivityForResult(intent, DETAILS_REQUEST_CODE);
        }
    };

    // On compose intent finish or details finish (return to timeline)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Get tweet from compose intent, and add to recycler view by adding to dataset and notifying adapter
            assert data != null;
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyDataSetChanged();
            // Scroll to new published tweet
            rvTweets.smoothScrollToPosition(0);
        } if (requestCode == DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            // Get the tweet info from the details activity and let the adapter know an update has occurred
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            int pos = data.getIntExtra("position", 0);
            tweets.set(pos, tweet);
            adapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // Get all the tweets from the client GET into an array and let the adapter know the data set has updated
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "Populated timeline");
                } catch (JSONException e) {
                    Log.e(TAG, "Error populating timeline: " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, response.toString());
                Log.e(TAG, "onFailure to populate timeline: " + throwable.toString());
            }
        });
    }

    public void onLogoutClicked() {
        client.clearAccessToken();
        finish();
    }

    // get tweets from timeline using the client GET, makes sure to stop the refreshing animation when done
    public void fetchTimelineasync(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                // Replace adapter items with new refreshed items
                try {
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "fetchTimelineasync: " + e.toString());
                }
                srlTimelineRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Fetch timeline error: " + throwable.toString());
            }
        });
    }
}