package com.personapplication.stijn.marsroverphotos.Presentation;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.R;

public class PhotoDetailActivity extends AppCompatActivity {
    private static final String TAG = PhotoDetailActivity.class.getSimpleName();

    private ImageView mImageView;

    private TextView mCameraNameTextView;
    private TextView mRoverNameTextView;
    private TextView mSolDayTextView;
    private TextView mEarthDayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        Intent deliveredIntent = getIntent();

        //Check if a JSON string for the photo was included
        if(deliveredIntent.getExtras() != null) {

            //Turn the JSON string back into a Photo object
            String JSONphoto = deliveredIntent.getExtras().getString(Config.JSON_PHOTO);
            Photo photo = new Gson().fromJson(JSONphoto, Photo.class);

            Log.d(TAG, "Activity started with Photo ID " + photo.getID());

            mCameraNameTextView = findViewById(R.id.tv_detail_camera_name);
            mCameraNameTextView.setText(photo.getFullCameraName());

            mImageView = findViewById(R.id.iv_photo_detail);
            Glide.with(this).load(photo.getImg_url()).into(mImageView);

            //If the detail activity is in landscape mode, show extra Photo information
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mRoverNameTextView = findViewById(R.id.tv_detail_rover_name);
                mRoverNameTextView.setText(photo.getRover().getName());

                mSolDayTextView = findViewById(R.id.tv_detail_sol_day);
                mSolDayTextView.setText(String.valueOf(photo.getSol()));

                mEarthDayTextView = findViewById(R.id.tv_detail_earth_day);
                mEarthDayTextView.setText(photo.getEarthDate());
            }

        }
    }
}
