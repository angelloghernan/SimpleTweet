package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TweetDetailsActivity extends AppCompatActivity {

    // Keeps a 2:1 aspect ratio assuming there are 4 images
    // Then, the first image has a 7:8 aspect ratio if there are 3 images
    // Then, the second image also has a 7:8 aspect ratio if there are 4 images
    // Finally, the first image will have a 2:1 aspect ration if it is the only image, with double the size
    public static final int ORIGINAL_IV_WIDTH = 194;
    public static final int ORIGINAL_IV_HEIGHT = 97;
    public static final int IV_SEPARATE_MARGIN = 2;

    public static final int TIME_INDEX = 3;
    public static final int MONTH_INDEX = 1;
    public static final int DAY_INDEX = 2;
    public static final int YEAR_INDEX = 5;
    public static final int AM_PM_INDEX = 6;


    public static final String TAG = "TweetDetailsActivity";

    Tweet tweet;
    TwitterClient client;
    int pos;

    ImageView ivDetailsImage1;
    ImageView ivDetailsImage2;
    ImageView ivDetailsImage3;
    ImageView ivDetailsImage4;

    ImageView ivDetailsProfile;

    TextView tvDetailsUsername;
    TextView tvDetailsUserAt;
    TextView tvDetailsContent;
    TextView tvDetailsDateTimeSource;
    TextView tvDetailsLikeCount;
    TextView tvDetailsRetweetCount;

    ImageButton ibDetailsLike;
    ImageButton ibDetailsRetweet;
    ImageButton ibDetailsReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        // Unwrap tweet passed in through intent and through parceling
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        pos = getIntent().getIntExtra("position", 0);
        client = TwitterApp.getRestClient(this);

        List<ImageView> imageViewHolder = new ArrayList<>();

        // Find all the views by id, add image views to list
        // Note to self: use the view library next time!!!
        ivDetailsImage1 = findViewById(R.id.ivDetailsImage1);
        ivDetailsImage2 = findViewById(R.id.ivDetailsImage2);
        ivDetailsImage3 = findViewById(R.id.ivDetailsImage3);
        ivDetailsImage4 = findViewById(R.id.ivDetailsImage4);

        imageViewHolder.add(ivDetailsImage1);
        imageViewHolder.add(ivDetailsImage2);
        imageViewHolder.add(ivDetailsImage3);
        imageViewHolder.add(ivDetailsImage4);

        ivDetailsProfile = findViewById(R.id.ivDetailsProfile);

        // Load pfp into profile picture image view
        Glide.with(this)
                .load(tweet.user.publicImageUrl)
                .circleCrop()
                .into(ivDetailsProfile);

        tvDetailsUserAt = findViewById(R.id.tvDetailsUserAt);
        tvDetailsContent = findViewById(R.id.tvDetailsContent);
        tvDetailsUsername = findViewById(R.id.tvDetailsUsername);
        tvDetailsDateTimeSource = findViewById(R.id.tvDetailsDateTimeSource);
        tvDetailsUsername.setText(tweet.user.name);
        tvDetailsUserAt.setText(String.format("@%s", tweet.user.screenName));
        tvDetailsContent.setText(tweet.body);

        ibDetailsLike = findViewById(R.id.ibDetailsLike);
        ibDetailsReply = findViewById(R.id.ibDetailsReply);
        ibDetailsRetweet = findViewById(R.id.ibDetailsRetweet);
        ibDetailsLike = findViewById(R.id.ibDetailsLike);

        if (tweet.liked) {
            ibDetailsLike.setSelected(true);
        }
        ibDetailsRetweet.setSelected(tweet.retweeted);




        tvDetailsRetweetCount = findViewById(R.id.tvDetailsRetweetCount);
        tvDetailsLikeCount = findViewById(R.id.tvDetailsLikeCount);

        TweetInteractions tweetInteractions = new TweetInteractions(this, ibDetailsLike, ibDetailsRetweet, ibDetailsReply,
                client, tweet);

        ibDetailsLike.setOnClickListener(tweetInteractions);
        ibDetailsRetweet.setOnClickListener(tweetInteractions);

        // Make style for retweet count and like count (bold number, unbolded word)
        SpannableStringBuilder rtStyle = new SpannableStringBuilder(String.format("%s Retweets", Integer.toString(tweet.retweetCount)));
        SpannableStringBuilder likeStyle = new SpannableStringBuilder(String.format("%s Likes", Integer.toString(tweet.likeCount)));
        final ForegroundColorSpan numFCS = new ForegroundColorSpan(Color.rgb(0,0,0));
        final StyleSpan numSS = new StyleSpan(Typeface.BOLD);

        // Set style only for the numbers, index 0 - end of number of digits of retweet/like count
        rtStyle.setSpan(numFCS, 0, numDigits(tweet.retweetCount), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        rtStyle.setSpan(numSS, 0, numDigits(tweet.retweetCount), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        likeStyle.setSpan(numFCS, 0, numDigits(tweet.likeCount), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        likeStyle.setSpan(numSS, 0, numDigits(tweet.likeCount), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        tvDetailsRetweetCount.setText(rtStyle);
        tvDetailsLikeCount.setText(likeStyle);

        // format the date and time info from tweet json into the same format as twitter's client formatting
        String[] dateTimeInfo = {};

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setTimeZone(TimeZone.getDefault());
        sf.setLenient(true);

        // Convert to new format by using two different SDFs, get each individual element in an array
        // Need this one for AM/PM in time under tweet (aa)
        SimpleDateFormat sf2 = new SimpleDateFormat("EEE MMM dd h:mm ZZZZZ yyyy aa", Locale.ENGLISH);
        sf2.setTimeZone(TimeZone.getDefault());
        sf2.setLenient(true);

        try {
            dateTimeInfo = sf2.format(Objects.requireNonNull(sf.parse(tweet.timeStamp))).split(" ");
        } catch (ParseException e) {
            Log.e(TAG, "error decoding timestamp: " + e.toString());
        }

        // Finally put all the information in the timestamp so that it matches regular twitter layout
        try {
            tvDetailsDateTimeSource.setText(String.format("%s %s · %s %s %s · %s", dateTimeInfo[TIME_INDEX],
                    dateTimeInfo[AM_PM_INDEX], dateTimeInfo[MONTH_INDEX], dateTimeInfo[DAY_INDEX],
                    dateTimeInfo[YEAR_INDEX], tweet.source));

        } catch (NumberFormatException e) {
            Log.e(TAG, "error setting date and time on tweet: " + e.toString());
        }

        // Needed to resize image layout after this
        for (int i = 0; i < imageViewHolder.size(); i++) {
            imageViewHolder.get(i).requestLayout();
        }

        // Change layout based on how many images are embedded in the tweet to match Twitter's formatting
        // Note: the following code is quite messy, but I couldn't find a suitable library for this, so this is my best attempt
        switch(tweet.imageUrls.size()) {
            case 1:
                for (int i = 1; i < imageViewHolder.size(); i++) {
                    imageViewHolder.get(i).setVisibility(View.GONE);
                }
                ivDetailsImage1.getLayoutParams().width = dpToPx(ORIGINAL_IV_WIDTH * 2, this);
                ivDetailsImage1.getLayoutParams().height = dpToPx(ORIGINAL_IV_HEIGHT * 2 + IV_SEPARATE_MARGIN, this);
                Glide.with(this)
                        .load(tweet.imageUrls.get(0))
                        .into(ivDetailsImage1);
                break;
            case 2:
                for (int i = 2; i < imageViewHolder.size(); i++) {
                    imageViewHolder.get(i).setVisibility(View.GONE);
                }
                ivDetailsImage1.getLayoutParams().height = dpToPx(ORIGINAL_IV_HEIGHT * 2 + IV_SEPARATE_MARGIN, this);
                ivDetailsImage2.getLayoutParams().height = dpToPx(ORIGINAL_IV_HEIGHT * 2 + IV_SEPARATE_MARGIN, this);

                for (int i = 0; i < 2; i++) {
                    Glide.with(this)
                            .load(tweet.imageUrls.get(i))
                            .into(imageViewHolder.get(i));
                }
                break;
            case 3:
                ivDetailsImage3.setVisibility(View.GONE);
                ivDetailsImage1.getLayoutParams().height = dpToPx(ORIGINAL_IV_HEIGHT * 2 + IV_SEPARATE_MARGIN, this);
                ivDetailsImage4.getLayoutParams().width = dpToPx(ORIGINAL_IV_WIDTH, this);
                Glide.with(this)
                        .load(tweet.imageUrls.get(0))
                        .into(ivDetailsImage1);
                Glide.with(this)
                        .load(tweet.imageUrls.get(1))
                        .into(ivDetailsImage2);
                Glide.with(this)
                        .load(tweet.imageUrls.get(2))
                        .into(ivDetailsImage4);
                break;
            case 4:
                for (int i = 0; i < imageViewHolder.size(); i++) {
                    Glide.with(this)
                            .load(tweet.imageUrls.get(i))
                            .into(imageViewHolder.get(i));
                }
                break;
            default:
                for (int i = 0; i < imageViewHolder.size(); i++) {
                    imageViewHolder.get(i).setVisibility(View.GONE);
                }
                break;
        }
    }

    // When back is pressed, send back the tweet information and position through intent so that the information
    // (retweet, like status) can update
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("tweet", Parcels.wrap(tweet));
        intent.putExtra("position", pos);
        setResult(RESULT_OK, intent);
        finish();
    }

    // Convert dp to px so we can resize image views correctly
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // use only for getting like/rt number digit count
    public static int numDigits(int n) {
        if (n > 0)
            return (int)(Math.log10(n)+1);
        else
            return 1;
    }
}