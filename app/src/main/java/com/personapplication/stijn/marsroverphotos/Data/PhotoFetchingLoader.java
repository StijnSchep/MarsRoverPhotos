package com.personapplication.stijn.marsroverphotos.Data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.personapplication.stijn.marsroverphotos.Business.JsonParser;
import com.personapplication.stijn.marsroverphotos.Config.Config;
import com.personapplication.stijn.marsroverphotos.Domain.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PhotoFetchingLoader extends AsyncTaskLoader<List<Photo>> {
    private static final String TAG = PhotoFetchingLoader.class.getSimpleName();

    private Bundle args;
    private List<Photo> photos = new ArrayList<>();

    //The class that uses this loader
    private LoaderListener listener;

    public PhotoFetchingLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;

        Log.d(TAG, "Created a new Loader");
    }

    //Interface used to access the activity's showProgressBar() and showResult() methods
    public interface LoaderListener {
        void showProgressBar();
        void showResult();
    }

    public void setListener(LoaderListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onStartLoading() {

        //If no arguments were given, simply return to the callback class
        if (args == null) {
            Log.d(TAG, "Loader was given an empty bundle, returning");
            return;
        }

        //If the list with existing photos is not empty, then no API call should be made
        if (!photos.isEmpty()) {
            Log.d(TAG, "List with existing photos is not empty, delivering the photos");
            deliverResult(photos);
        } else {
            listener.showProgressBar();

            Log.d(TAG, "List with existing photos is empty, forcing loader to reload");
            forceLoad();
        }
    }

    //Get the JSON response from the API with the URL from the bundle
    @Override
    public List<Photo> loadInBackground() {
        Log.d(TAG, "Background task started");

        String URLString = args.getString(Config.SEARCH_QUERY_URL_EXTRA);

        try {
            URL url = new URL(URLString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            InputStream in = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                String response = scanner.next();
                Log.d(TAG, "Response from background task: " + response);

                httpURLConnection.disconnect();
                return JsonParser.parsePhotos(response);
            }

            httpURLConnection.disconnect();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    //Deliver the given photos to the callback class' onLoadFinished method
    @Override
    public void deliverResult(@Nullable List<Photo> photos) {
        Log.d(TAG, "deliverResult was called");

        if(photos != null) {
            Log.d(TAG, "Result is not null, adding them to the existing list of photos");
            List<Photo> temp = new ArrayList<>(photos);

            this.photos.clear();
            this.photos.addAll(temp);
        }

        super.deliverResult(photos);
    }
}
