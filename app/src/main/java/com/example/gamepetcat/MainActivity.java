package com.example.gamepetcat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String DATA_PET_CAT = "dataPetCat.txt";

    // Create an array of resource IDs for the images
    private int[] arrayMaidImages = {R.drawable.maid_blondie_1_min,
            R.drawable.maid_blondie_2_min,
            R.drawable.maid_redheadie_1_min,
            R.drawable.maid_redheadie_2_min};
    private List<String> listOfPhrases = Arrays.asList("Purr-fect", "Claw-ver move", "Purr-ty", "Paw-some");
    ThreadButtonCombo threadButtonCombo = null;
    private int clickCounter = 0;
    private int imgIDmaid = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if file exists - load file
        File file = new File(getFilesDir() + "/" + DATA_PET_CAT);
        if (file.exists()){
            loadData();
        }
        TextView tvPhrase = findViewById(R.id.textView_CatPhrases);
        tvPhrase.setText("Fur Real? How long should I wait?");
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
        // Check if there are images available in the resources array
        if (arrayMaidImages.length > 0){
            imgIDmaid = (imgIDmaid + 1) % arrayMaidImages.length;
            imgMaid.setImageResource(arrayMaidImages[imgIDmaid]);
        } else {
            //Handling exception with default image
            imgMaid.setImageResource(R.drawable.maid_blondie_1_min);
        }

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
        this.clickCounter = 0;
        tvCounter.setText("Pets given: " + clickCounter);
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