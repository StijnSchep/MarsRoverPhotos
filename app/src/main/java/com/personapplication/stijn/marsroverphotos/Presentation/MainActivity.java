package com.personapplication.stijn.marsroverphotos.Presentation;

import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.personapplication.stijn.marsroverphotos.Business.PhotoAdapter;
import com.personapplication.stijn.marsroverphotos.Business.URLBuilder;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Data.PhotoFetchingLoader;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PhotoFetchingLoader.LoaderListener,
        LoaderManager.LoaderCallbacks<List<Photo>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mLoadingIndicator;
    private RecyclerView mPhotoRecyclerView;

    private List<Photo> photos = new ArrayList<>();

    private boolean showToast = true;

    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate was called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_response);
        mPhotoRecyclerView = findViewById(R.id.rv_photo_list);

        //Set LayoutManager based on screen orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        photoAdapter = new PhotoAdapter(photos);
        mPhotoRecyclerView.setAdapter(photoAdapter);

        //Check if a loader exists, Toast should not be shown if the result is not new
        Loader previousLoader = getSupportLoaderManager().getLoader(Config.PHOTO_FETCHING_LOADER);
        if(previousLoader == null) {
            Log.d(TAG, "No previous loader was found");
        } else {
            Log.d(TAG, "Previous loader was found");
            showToast = false;
        }

        //Get the photos with the specified rover
        getPhotosFromRover(Config.OPPORTUNITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rover_curiosity:
                restartLoader(Config.CURIOSITY);
                return true;

            case R.id.rover_opportunity:
                restartLoader(Config.OPPORTUNITY);
                return true;

            case R.id.rover_spirit:
                restartLoader(Config.SPIRIT);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    //When the user selects a rover, restart the loader with new data
    private void restartLoader(String rover) {
        Log.d(TAG, "Started process to restart loader...");
        showToast = true;

        URL photoURL = URLBuilder.buildPhotoFetchingURL(rover);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(Config.SEARCH_QUERY_URL_EXTRA, photoURL.toString());

        //Restart the loader with new data
        getSupportLoaderManager().restartLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
    }

    /* Starts the background task to get photos from a given rover */
    private void getPhotosFromRover(String rover) {
        Log.d(TAG, "Started process to fetch images...");

        URL photoURL = URLBuilder.buildPhotoFetchingURL(rover);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(Config.SEARCH_QUERY_URL_EXTRA, photoURL.toString());

        //Initialize the loader
        getSupportLoaderManager().initLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
    }

    // Give an indication that the app is loading
    public void showProgressBar() {
        Log.d(TAG, "Now showing the progress bar");

        mPhotoRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    // Hide the progress bar, show the RecyclerView
    public  void showResult() {
        Log.d(TAG, "Now showing the response");

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mPhotoRecyclerView.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<List<Photo>> onCreateLoader(int i, @Nullable Bundle bundle) {
        PhotoFetchingLoader loader = new PhotoFetchingLoader(this, bundle);
        loader.setListener(this);

        Log.d(TAG, "Returning a new loader...");
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Photo>> loader, List<Photo> photos) {
        Log.d(TAG, "onLoadFinished was called");

        int size = photos.size();
        int total = photos.get(0).getRover().getPhotoAmount();
        Log.d(TAG, "PhotoFetchingLoader returned a list with size " + size);

        if(showToast) {
            Toast toast = Toast.makeText(this, "Fetched " + size + " out of " + total + " photos", Toast.LENGTH_LONG);
            toast.show();
        }

        showResult();

        this.photos.clear();
        this.photos.addAll(photos);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Photo>> loader) {
        Log.d(TAG, "onLoaderReset was called");
    }
}
