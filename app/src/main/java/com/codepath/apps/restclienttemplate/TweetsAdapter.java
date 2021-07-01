package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivMediaImage = itemView.findViewById(R.id.ivMediaImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUserAt = itemView.findViewById(R.id.tvUserAt);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimePosted = itemView.findViewById(R.id.tvTimePosted);
            itemView.setOnClickListener(this);

        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvTimePosted.setText(tweet.createdAt);
            tvUserAt.setText("@" + tweet.user.screenName);
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .circleCrop()
                    .into(ivProfileImage);
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
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
