package com.personapplication.stijn.marsroverphotos.Presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.personapplication.stijn.marsroverphotos.R;

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_);
        setTitle(R.string.preferences);
    }
}
