package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;
import java.util.Locale;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    private static final String TAG = "TweetsAdapter";
    private final TwitterClient client;
    Context context;
    List<Tweet> tweets;
    AdapterCallback callback;

    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client, AdapterCallback callback) {
        this.context = context;
        this.tweets = tweets;
        this.client = client;
        this.callback = callback;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        ImageView ivMediaImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimePosted;
        TextView tvUserAt;
        TextView tvLikeCount;
        TextView tvRetweetCount;
        ImageButton ibLike;
        ImageButton ibReply;
        ImageButton ibRetweet;
        TweetInteractions tweetInteractions;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUserAt = itemView.findViewById(R.id.tvUserAt);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimePosted = itemView.findViewById(R.id.tvTimePosted);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibReply = itemView.findViewById(R.id.ibReply);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
        }

        public void bind(Tweet tweet) {
            tweetInteractions = new TweetInteractions(context, ibLike, ibRetweet, ibReply, client, tweet);
            ibLike.setOnClickListener(tweetInteractions);
            ibRetweet.setOnClickListener(tweetInteractions);
            itemView.setOnClickListener(this);
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvTimePosted.setText(tweet.createdAt);
            tvUserAt.setText(String.format("@%s", tweet.user.screenName));

            if (tweet.likeCount == 0) {
                tvLikeCount.setText("");
            } else {
                tweetInteractions.updateCounterView(tvLikeCount, tweet.likeCount);
            }
            if (tweet.retweetCount == 0) {
                tvRetweetCount.setText("");
            } else {
                tvRetweetCount.setText(String.format(Locale.ENGLISH, "%d", tweet.retweetCount));
            }

            ibLike.setSelected(tweet.liked);
            ibRetweet.setSelected(tweet.retweeted);

            // Load profile picture into profile image view
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .circleCrop()
                    .into(ivProfileImage);

            // The tweet has an image, show the first image
            // TODO: make other images show up as well just like in details activity -- this will also be rough
            if (tweet.imageUrls.size() != 0) {
                ivMediaImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.imageUrls.get(0))
                        .fitCenter()
                        .transform(new RoundedCorners(10))
                        .placeholder(R.drawable.placeholder_twit)
                        .into(ivMediaImage);
            } else {
                ivMediaImage.setVisibility(View.GONE);
            }
        }

        @Override
        // When the view is clicked, run this. Checks what is clicked and goes from there.
        // If none of the buttons are clicked it will bring up the details activity
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                final Tweet tweet = tweets.get(position);
                callback.onAdapterSelected(position, tweet);
            }
        }
    }


    // clear tweet list so that a new set can be fetched
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public interface AdapterCallback {
        void onAdapterSelected(int pos, Tweet tweet);
    }
}
