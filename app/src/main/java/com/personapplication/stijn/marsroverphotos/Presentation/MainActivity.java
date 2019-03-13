package com.personapplication.stijn.marsroverphotos.Presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
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

import com.personapplication.stijn.marsroverphotos.Business.LocaleHandler;
import com.personapplication.stijn.marsroverphotos.Business.PhotoAdapter;
import com.personapplication.stijn.marsroverphotos.Business.PreferenceHandler;
import com.personapplication.stijn.marsroverphotos.Business.Tools.URLBuilder;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Data.PhotoFetchingLoader;
import com.personapplication.stijn.marsroverphotos.Domain.LoaderMode;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PhotoFetchingLoader.LoaderListener,
        LoaderManager.LoaderCallbacks<List<Photo>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Views
    private ProgressBar mLoadingIndicator;
    private RecyclerView mPhotoRecyclerView;

    //Flags
    private boolean showToast = true;
    private boolean languagePreferenceChanged;

    //Data
    private List<Photo> photos = new ArrayList<>();
    private PhotoAdapter photoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHandler.setLocale(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate was called");

        mLoadingIndicator = findViewById(R.id.pb_loading_response);
        mPhotoRecyclerView = findViewById(R.id.rv_photo_list);


        /* -- RecyclerView setup -- */

        photoAdapter = new PhotoAdapter(photos);
        mPhotoRecyclerView.setAdapter(photoAdapter);

        //Set LayoutManager based on screen orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        /* -- /RecyclerView setup -- */


        /* -- SharedPreferences setup -- */

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        /* -- /SharedPreferences setup -- */


        //If a previous loader exists, then the data is not new and no toast should be shown
        if(getSupportLoaderManager().getLoader(Config.PHOTO_FETCHING_LOADER) != null) {
            showToast = false;
        }


        //Get the photos
        getPhotos(LoaderMode.INIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroying activity, unregistering sharedpreferencelistener...");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume was called");


        //Check if the language preference changed
        //If YES, refresh the activity to show changes
        if(languagePreferenceChanged) {
            languagePreferenceChanged = false;

            Log.d(TAG, "Language preference changed, refreshing activity...");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
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



    /* ---- BEGIN OPTIONS MENU METHODS ---- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Creating the options menu...");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected was called");

        //item is null when the menu has to be closed with no user input
        if(item == null) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.rover_preferences:
                Intent startPreferencesActivity = new Intent(this, PreferenceActivity.class);
                startActivity(startPreferencesActivity);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    /* ---- END OPTIONS MENU METHODS ---- */



    /* ---- BEGIN LOADER METHODS ---- */

    /* Starts the background task to get photos from a given rover */
    private void getPhotos(LoaderMode mode) {
        Log.d(TAG, "Started process to fetch images...");

        String rover = PreferenceHandler.getRoverPreference(this);

        URL photoURL = URLBuilder.buildPhotoFetchingURL(rover);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(Config.SEARCH_QUERY_URL_EXTRA, photoURL.toString());


        switch (mode) {
            case RESTART:
                Log.d(TAG, "Restarting loader...");

                showToast = true;
                getSupportLoaderManager().restartLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
                break;

            case INIT:
                Log.d(TAG, "Initializing loader...");

                getSupportLoaderManager().initLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
        }
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
            String part1 = getResources().getString(R.string.fetched_photo_toast_1);
            String part2 = getResources().getString(R.string.fetched_photo_toast_2);
            String part3 = getResources().getString(R.string.fetched_photo_toast_3);
            Toast toast = Toast.makeText(this,
                    part1 + " " + size + " " + part2 +" "+ total + " " + part3 + " ",
                    Toast.LENGTH_LONG);
            toast.show();

            showToast = false;
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

    /* ---- END LOADER METHODS ---- */


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "A preference has changed!");

        if(key.equals(getString(R.string.preference_rover))) {
            Log.d(TAG, "The preference for rover has changed");

            getPhotos(LoaderMode.RESTART);
        } else if(key.equals(getString(R.string.preference_language))) {
            Log.d(TAG, "The preference for the language has changed, allowing refresh on resume");

            languagePreferenceChanged = true;
        }
    }
}
