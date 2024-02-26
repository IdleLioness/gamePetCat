package com.example.gamepetcat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String DATA_PET_CAT = "dataPetCat.txt";

    // Create an array of resource IDs for the images
    private List<Maid> maids = new ArrayList<Maid>();
    private int currentMaidIndex = 0;
    private List<String> listOfPhrases = Arrays.asList("Purr-fect", "Claw-ver move", "Purr-ty", "Paw-some");
    ThreadButtonCombo threadButtonCombo = null;
    private Thread autosaveThread = null;
    private static final long AUTOSAVE_INTERVAL_MS = 2 * 1000; // Autosave interval: 2 seconds
    private boolean autosaveEnabled = true;

    private static int clickCounter = 0;
    private TextView tvCounter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeMaids();

        //if file exists - load file
        File file = new File(getFilesDir() + "/" + DATA_PET_CAT);
        if (file.exists()){
            loadData();
        }

        tvCounter = findViewById(R.id.textView_Counter);

        /*/ Get SharedPreferences instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the current value of counter
        int counter = preferences.getInt("counter", clickCounter);

        // Update the TextView with the counter value
        tvCounter.setText("Pets given: " + counter);//*/

        TextView tvPhrase = findViewById(R.id.textView_CatPhrases);
        tvPhrase.setText("Fur Real? How long should I wait?");

        if (autosaveThread == null){
            autosaveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (autosaveEnabled) {
                        try {
                            // Autosave logic
                            saveData();
                            Thread.sleep(AUTOSAVE_INTERVAL_MS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            autosaveThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autosaveEnabled = false;
        try {
            autosaveThread.join(); // Wait for the autosave thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initializeMaids(){
        Maid blondie = new Maid(R.drawable.maid_blondie_1_min,
                R.drawable.maid_blondie_2_min);
        Maid redheadie = new Maid(R.drawable.maid_redheadie_1_min,
                R.drawable.maid_redheadie_2_min);
        maids.add(blondie);
        maids.add(redheadie);
    }

    public void buttonClicked (View v){
        clickCounter++;
        TextView tvCombo = findViewById(R.id.textViewCombo);

        TextView tvCounter = findViewById(R.id.textView_Counter);
        tvCounter.setText("Pets given: " + clickCounter);

        int randomIndex = (int) (Math. random() * listOfPhrases.size());
        TextView tvPhrase = findViewById(R.id.textView_CatPhrases);
        tvPhrase.setText(listOfPhrases.get(randomIndex));

        ImageButton imgMaid = findViewById(R.id.imageButtonMaid);

        imgMaid.setImageResource(maids.get(currentMaidIndex).nextLook());

        // if thread was not finished - finish thread safely
        try {
            if (threadButtonCombo != null){
                threadButtonCombo.setShouldExit(true);
                threadButtonCombo.join();
                threadButtonCombo = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // start thread to menage combo after and break it after 3 sec inactivity
        threadButtonCombo = new ThreadButtonCombo(tvCombo);
        threadButtonCombo.start();
    }

    public void launchSettings(View v){
        Log.d("fileSavedTo", getFilesDir() + "/" + DATA_PET_CAT);
        //System.out.println("launchSettings");
        Intent i = new Intent(this, SettingsActivity.class);
        //Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }

    public synchronized void updateCombo(int value){
        TextView tvCombo = findViewById(R.id.textViewCombo);
        tvCombo.setText("Combo: " + value + " !");
        if (value != 0)
            tvCombo.setVisibility(View.VISIBLE);
        else
            tvCombo.setVisibility(View.INVISIBLE);
    }


    public void resetClickCounter(View v){
        TextView tvCounter = findViewById(R.id.textView_Counter);
        clickCounter = 0;
        tvCounter.setText("Pets given: " + clickCounter);
    }

    //changing index by increasing and image by index
    public void nextMaidRightArrow(View v){
        currentMaidIndex = (currentMaidIndex + 1) % maids.size();
        //change image
        ImageButton imgMaid = findViewById(R.id.imageButtonMaid);
        imgMaid.setImageResource(maids.get(currentMaidIndex).nextLook());
    }

    //changing index by decreasing and image by index
    public void nextMaidLeftArrow(View v){
        if (currentMaidIndex <= 0)
            currentMaidIndex = maids.size();
        currentMaidIndex = currentMaidIndex - 1;
        //change image
        ImageButton imgMaid = findViewById(R.id.imageButtonMaid);
        imgMaid.setImageResource(maids.get(currentMaidIndex).nextLook());
    }

    public void saveData(View v) {
        //input data into text
        String text = Integer.toString(clickCounter);
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(DATA_PET_CAT, MODE_PRIVATE);
            //pass the text in bytes
            fos.write(text.getBytes());
            //pop-up windows
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + DATA_PET_CAT, Toast.LENGTH_LONG).show();
            Log.d("fileSavedTo", getFilesDir() + "/" + DATA_PET_CAT);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveData() {
        //input data into text
        String text = Integer.toString(clickCounter);
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(DATA_PET_CAT, MODE_PRIVATE);
            //pass the text in bytes
            fos.write(text.getBytes());

            //pop-up windows
            final String filePath = getFilesDir() + "/" + DATA_PET_CAT;
            Log.d("fileSavedTo", filePath);
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //pop-up windows
                    Toast.makeText(getApplicationContext(), "Saved to " + filePath, Toast.LENGTH_LONG).show();
                    Log.d("fileSavedTo", filePath);
                }
            });*/

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadData() {
        FileInputStream fis = null;
        try{
            fis = openFileInput(DATA_PET_CAT);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            text = br.readLine();

            //convert data from buffer into StringBuilder into String into Integer
            this.clickCounter = Integer.parseInt(String.valueOf(sb.append(text)));

            TextView tvCounter = findViewById(R.id.textView_Counter);
            tvCounter.setText("Pets given: " + clickCounter);

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}