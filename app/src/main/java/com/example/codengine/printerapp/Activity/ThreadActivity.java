package com.example.codengine.printerapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.codengine.printerapp.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadActivity extends AppCompatActivity {

    Button click;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

       /* makeLog("main oncreate");*/
        click = findViewById(R.id.click);

        //makeOnlyLog("HIII THERE");
        makeOnlyLog(""+Runtime.getRuntime().availableProcessors());


        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());



        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Thread(new Runnable() {
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
                }).start();*/

                for (int i =0;i<20;i++){
                    threadPoolExecutor.execute(new Task(i));
                }
            }
        });
    }

    class Task implements Runnable{

        int i;
        Task(int i){
            this.i = i;
        }

        @Override
        public void run() {
            makeOnlyLog("Task Name : "+i+" Thread name: "+ Thread.currentThread().getName()+"\t id "+ Thread.currentThread().getId());
        }
    }

    public void makeLog(String msg){
        Log.e("Thread Id "+Thread.currentThread().toString()," msg "+msg);
    }

    public void makeOnlyLog(String msg){
        Log.e("ThreadActivity "," msg "+msg);
    }
}