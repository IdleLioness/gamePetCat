package com.example.gamepetcat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private int counter;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get SharedPreferences instance
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the current value of counter
        counter = preferences.getInt("counter", 0);
    }

    public void launchMain(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void resetClickCounter(View v){
        // Update the counter value
        counter = 0;

        // Update the counter value in SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("counter", counter);
        editor.apply();
    }
}