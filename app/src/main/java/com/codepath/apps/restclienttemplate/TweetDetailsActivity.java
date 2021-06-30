package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    // Keeps a 2:1 aspect ratio assuming there are 4 images
    // Then, the first image has a 7:8 aspect ratio if there are 3 images
    // Then, the second image also has a 7:8 aspect ratio if there are 4 images
    // Finally, the first image will have a 2:1 aspect ration if it is the only image, with double the size
    public final static int ORIGINAL_IV_WIDTH = 195;
    public final static int ORIGINAL_IV_HEIGHT = 98;

    Tweet tweet;

    ImageView ivDetailsImage1;
    ImageView ivDetailsImage2;
    ImageView ivDetailsImage3;
    ImageView ivDetailsImage4;

    ImageView ivDetailsProfile;

    TextView tvDetailsUsername;
    TextView tvDetailsUserAt;
    TextView tvDetailsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivDetailsImage1 = findViewById(R.id.ivDetailsImage1);
        ivDetailsImage2 = findViewById(R.id.ivDetailsImage2);
        ivDetailsImage3 = findViewById(R.id.ivDetailsImage3);
        ivDetailsImage4 = findViewById(R.id.ivDetailsImage4);

        ivDetailsProfile = findViewById(R.id.ivDetailsProfile);

        tvDetailsUserAt = findViewById(R.id.tvDetailsUserAt);
        tvDetailsContent = findViewById(R.id.tvDetailsContent);
        tvDetailsUsername = findViewById(R.id.tvDetailsUsername);


    }
}