package com.example.codengine.printerapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.codengine.printerapp.R;

public class ThreadActivity extends AppCompatActivity {

    Button click;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        makeLog("main oncreate");
        click = findViewById(R.id.click);



        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        makeLog("CLick");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    makeLog("Create 2");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }).start();
            }
        });
    }

    public void makeLog(String msg){
        Log.e("Thread Id "+Thread.currentThread().toString()," msg "+msg);
    }
}