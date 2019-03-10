package com.personapplication.stijn.marsroverphotos.Presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
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

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.personapplication.stijn.marsroverphotos.Business.PhotoAdapter;
import com.personapplication.stijn.marsroverphotos.Business.URLBuilder;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Data.PhotoFetchingLoader;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;
import com.personapplication.stijn.marsroverphotos.R;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.OutputKeys;

public class MainActivity extends AppCompatActivity implements
        PhotoFetchingLoader.LoaderListener,
        LoaderManager.LoaderCallbacks<List<Photo>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mLoadingIndicator;
    private RecyclerView mPhotoRecyclerView;

    private List<Photo> photos = new ArrayList<>();

    public static Locale sSystemLocale;

    private boolean showToast = true;
    private boolean languageChanged;

    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate was called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the language to the users preference
        sSystemLocale = Locale.getDefault();
        Log.d(TAG, "System language is: " + sSystemLocale.getLanguage());
        setLocale();

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

        //Get the photos
        getPhotos();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged was called");

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

        //If isNew is false, then the
        if(languageChanged) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Creating the options menu...");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected was called");

        switch (item.getItemId()) {
            case R.id.rover_preferences:
                Intent startPreferencesActivity = new Intent(this, PreferenceActivity.class);
                startActivity(startPreferencesActivity);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    //When the user selects a rover, restart the loader with new data
    private void restartLoader() {
        Log.d(TAG, "Started process to restart loader...");
        showToast = true;

        String rover = getRoverPreference();

        URL photoURL = URLBuilder.buildPhotoFetchingURL(rover);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(Config.SEARCH_QUERY_URL_EXTRA, photoURL.toString());

        //Restart the loader with new data
        Log.d(TAG, "Restarting loader...");
        getSupportLoaderManager().restartLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
    }

    /* Starts the background task to get photos from a given rover */
    private void getPhotos() {
        Log.d(TAG, "Started process to fetch images...");

        String rover = getRoverPreference();

        URL photoURL = URLBuilder.buildPhotoFetchingURL(rover);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(Config.SEARCH_QUERY_URL_EXTRA, photoURL.toString());

        //Initialize the loader
        Log.d(TAG, "Initializing loader...");
        getSupportLoaderManager().initLoader(Config.PHOTO_FETCHING_LOADER, queryBundle, this);
    }

    //Returns the rover that is specified in the SharedPreference
    private String getRoverPreference() {
        Log.d(TAG, "Fetching preferred rover...");
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        //Get preferred rover from SharedPreferences, return the value for 'curiosity' if no
        //rover is specified as preferred
        String rover = sharedPreferences.getString(Config.ROVER_PREFERENCE, Config.CURIOSITY);
        Log.d(TAG, "Preferred rover: " + rover);

        return rover;
    }

    private String getLanguagePreference() {
        Log.d(TAG, "Fetching preferred language...");
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        String language = sharedPreferences.getString(Config.LANGUAGE_PREFERENCE, Config.LANGUAGE_DEFAULT);
        Log.d(TAG, "Preferred language: " + language);

        return language;
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "A preference has changed!");

        if(key.equals(getString(R.string.preference_rover))) {
            Log.d(TAG, "The preference for rover has changed, restarting the Loader...");
            restartLoader();
        } else if(key.equals(getString(R.string.preference_language))) {
            Log.d(TAG, "The preference for the language has changed, setting language...");
            languageChanged = true;
            setLocale();
        }
    }

    private void setLocale() {
        Log.d(TAG, "changing language...");
        String language = getLanguagePreference();
        Locale locale;

        Configuration config = getResources().getConfiguration();

        if(language.equals(Config.LANGUAGE_DEFAULT)) {
            Log.d(TAG, "Changing language to system default");

           locale = sSystemLocale;
        } else if(language.equals(Config.LANGUAGE_ENGLISH)) {
            Log.d(TAG, "Changing language to english");

            locale = Locale.ENGLISH;
        } else if(language.equals(Config.LANGUAGE_DUTCH)) {
            Log.d(TAG, "Changing language to dutch");

            locale = new Locale("nl");
        } else {
            locale = sSystemLocale;
        }

        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
        else {
            config.locale = Locale.CANADA;
        }

        getBaseContext().getResources().updateConfiguration(config, null);
        onConfigurationChanged(config);
    }

}
