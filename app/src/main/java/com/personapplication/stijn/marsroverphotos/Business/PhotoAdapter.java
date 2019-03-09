package com.personapplication.stijn.marsroverphotos.Business;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.Presentation.PhotoDetailActivity;
import com.personapplication.stijn.marsroverphotos.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private static final String TAG = PhotoAdapter.class.getSimpleName();

    private List<Photo> photos;
    private Context context;

    //Adapter for the RecyclerView in MainActivity
    public PhotoAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int templateID = R.layout.list_photo_template;

        View v = inflater.inflate(templateID, viewGroup, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
        Log.d(TAG, "Binding viewholder on index " + i);

        Photo photo = photos.get(i);
        photoViewHolder.setPhoto(photo);

        int ID = photo.getID();
        String img_url = photo.getImg_url();

        photoViewHolder.mPhotoIDTextView.setText(String.valueOf(ID));

        Glide.with(context)
                .load(img_url)
                .into(photoViewHolder.mImageView);

        //If the device is in landscape mode, extra TextViews are shown and are filled here
        if(photoViewHolder.isLandscape) {
            photoViewHolder.mShortCameraName.setText(photo.getShortCameraName());
            photoViewHolder.mRoverNameTextView.setText(photo.getRover().getName());
            photoViewHolder.mEarthDayTextView.setText(photo.getEarthDate());
            photoViewHolder.mSolDayTextView.setText(String.valueOf(photo.getSol()));
        }

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    //The ViewHolder for a single image with information
    class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView mPhotoIDTextView;
        private Photo photo;

        //Extra TextViews that are only visible in Landscape mode
        private TextView mShortCameraName;
        private TextView mRoverNameTextView;
        private TextView mSolDayTextView;
        private TextView mEarthDayTextView;

        private boolean isLandscape;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.iv_photo);
            mPhotoIDTextView = itemView.findViewById(R.id.tv_image_id);

            //Check if the device is currently in landscape mode
            isLandscape = itemView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

            //Define extra TextViews if the device is in landscape mode
            if(isLandscape) {
                mShortCameraName = itemView.findViewById(R.id.tv_list_camera_name);
                mRoverNameTextView = itemView.findViewById(R.id.tv_list_rover_name);
                mSolDayTextView = itemView.findViewById(R.id.tv_list_sol_day);
                mEarthDayTextView = itemView.findViewById(R.id.tv_list_earth_day);
            }

            itemView.setOnClickListener(this);
        }

        void setPhoto(Photo photo) {
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PhotoDetailActivity.class);

            //Turn the Photo object into a JSON string
            String JSONphoto = new Gson().toJson(photo);

            //Include the JSON string in the intent
            intent.putExtra(Config.JSON_PHOTO, JSONphoto);

            //Start the Detail Activity with the intent
            v.getContext().startActivity(intent);
        }
    }

}
