package com.personapplication.stijn.marsroverphotos.Business;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.personapplication.stijn.marsroverphotos.Config.Config;

public class PreferenceHandler {
    private static final String TAG = PreferenceHandler.class.getSimpleName();

    /* -- STATIC METHODS -- */

    static String getLanguagePreference(Context context) {
        Log.d(TAG, "getLanguagePreference() was called");
        Log.d(TAG, "Searching for preferred language...");

        //Get the SharedPreferences for the given context
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        //Get the preferred language from SharedPreference, return 'default' if nothing is set
        String language = sharedPreferences.getString(Config.LANGUAGE_PREFERENCE, Config.LANGUAGE_DEFAULT);


        Log.d(TAG, "Found the language! Preferred language is: " + language);
        return language;
    }

    //Returns the rover that is specified in the SharedPreference
    public static String getRoverPreference(Context context) {
        Log.d(TAG, "getRoverPreference() was called");
        Log.d(TAG, "Searching for preferred rover...");

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        //Get preferred rover from SharedPreferences, default rover is 'curiosity'
        String rover = sharedPreferences.getString(Config.ROVER_PREFERENCE, Config.CURIOSITY);

        Log.d(TAG, "Found the rover! Preferred rover is: " + rover);
        return rover;
    }
}
