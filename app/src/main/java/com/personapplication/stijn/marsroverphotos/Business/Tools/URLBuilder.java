package com.personapplication.stijn.marsroverphotos.Business.Tools;

import android.net.Uri;
import android.util.Log;

import com.personapplication.stijn.marsroverphotos.Config.Config;

import java.net.MalformedURLException;
import java.net.URL;

public class URLBuilder {
    private static final String TAG = URLBuilder.class.getSimpleName();

    public static URL buildPhotoFetchingURL(String rover) {
        Log.d(TAG, "Started building URL to fetch photos...");

        URL url = null;

        //Builds the URI as follows:
        //BASE_URL/rovers/<rover>/photos?sol=<value>&API_KEY=<value>
        Uri builtUri = Uri.parse(Config.BASE_URL).buildUpon()
                .appendPath(Config.ROVERS)
                .appendPath(rover)
                .appendPath(Config.PHOTOS)
                .appendQueryParameter(Config.SOL_DATE, Config.SOL_DATE_VALUE)
                .appendQueryParameter(Config.API_KEY, Config.API_KEY_VALUE)
                .build();

        try {
            url = new URL(builtUri.toString());
            Log.d(TAG, "URL was built, value: " + url.toString());
        } catch(MalformedURLException e) {
            Log.e(TAG, e.getMessage());
            Log.d(TAG, "URL was not built, see error log");
        }

        Log.d(TAG, "Sending URL object to caller...");
        return url;
    }


}
