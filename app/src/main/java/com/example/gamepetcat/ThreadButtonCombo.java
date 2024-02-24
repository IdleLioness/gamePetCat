package com.example.gamepetcat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ThreadButtonCombo extends Thread{
    private boolean shouldExit = false;
    private static int combo = 0;
    private TextView textView;
    private Handler handler;

    public ThreadButtonCombo(TextView textView){
        this.textView = textView;
        handler = new Handler(Looper.getMainLooper());
    }

    public void setShouldExit(boolean shouldExit){this.shouldExit = shouldExit;}
    public int getCombo(){return combo;}

    public void run(){
        combo++;

        handler.post(new Runnable() {
            @Override
            public void run(){
                textView.setVisibility(View.VISIBLE);
                textView.setText("Combo: " + combo + "!");
            }
        });

        Log.d("comboBreaker", "Combo: " + combo);
        try {
            //thread waits for 1.5 sec
            for (int i = 0; i < 30; i++){
                if (shouldExit){
                    break;
                }
                Thread.sleep(50);
            }
            //if thread was not interupted
            if(!shouldExit){
                combo = 0;

                handler.post(new Runnable() {
                    @Override
                    public void run(){
                        textView.setVisibility(View.INVISIBLE);
                        textView.setText("Combo: " + combo + "!");
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        } finally {
            Log.d("comboBreaker", "combo: " + combo);
        }
    }
}
