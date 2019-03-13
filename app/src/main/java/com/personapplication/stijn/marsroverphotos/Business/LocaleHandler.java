package com.personapplication.stijn.marsroverphotos.Business;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import com.personapplication.stijn.marsroverphotos.Config.Config;

import java.util.Locale;


public class LocaleHandler {
    private static final String TAG = LocaleHandler.class.getSimpleName();

    public static void setLocale(Context context) {
        Log.d(TAG, "LocaleHandler.setLocale() was called");
        Log.d(TAG, "Setting locale...");

        Locale locale;
        switch(PreferenceHandler.getLanguagePreference(context)) {
            case Config.LANGUAGE_ENGLISH:
                Log.d(TAG, "Prepared english Locale!");

                locale = new Locale("en");
                break;

            case Config.LANGUAGE_DUTCH:
                Log.d(TAG, "Prepared dutch Locale!");
                locale = new Locale("nl");
                break;

                default:
                    Log.d(TAG, "Prepared default Locale!");
                    locale = Locale.getDefault();
        }

        Configuration activityConfig = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activityConfig.setLocale(locale);
        }
        else {
            activityConfig.locale = Locale.CANADA;
        }

        context.getResources().updateConfiguration(activityConfig, null);
    }

}
