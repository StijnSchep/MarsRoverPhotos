package com.personapplication.stijn.marsroverphotos.Presentation;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.personapplication.stijn.marsroverphotos.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}
