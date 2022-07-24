package com.example.codengine.printerapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.codengine.printerapp.PrinterEssentials.ThreadManager.Manager;
import com.example.codengine.printerapp.R;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadActivity extends AppCompatActivity {

    Button click;
    Thread thread;
    CountDownTimer countDownTimer;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

       /* makeLog("main oncreate");*/
        click = findViewById(R.id.click);


        countDownTimer = new CountDownTimer(1200000,2000) {
            @Override
            public void onTick(long l) {
                makeOnlyLog("Mgr "+Manager.getManagerInstance().getStatus());
            }

            @Override
            public void onFinish() {

            }
        };

       // countDownTimer.start();

        //makeOnlyLog("HIII THERE");
        //makeOnlyLog(""+Runtime.getRuntime().availableProcessors());


       // ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());




        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i =0;i<10;i++){
                    //Manager.getManagerInstance().runTask(new Task("_"+i+"_"));
                    MyThread thread = new MyThread("_"+i+"_");
                    thread.start();
                    i++;
                }
            }
        });
    }


    class MyThread extends Thread{

        String nm ="";
        public MyThread(String nm){
            this.nm = nm;
        }
        @Override
        public void run() {
            super.run();
            try {
                Log.e(nm+"","Will sleep");
                Thread.sleep(1000);
                Log.e(nm+"","Will resume");

               // Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                Log.e(nm+" is ","Interrupted");
                e.printStackTrace();
            }
        }

        @Override
        protected void finalize() {
            try {
                super.finalize();
                Log.e(nm+"","IS BEING FINALIZED");
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

    class Task implements Runnable{
        String nm ="";
        Task(String nm){
            this.nm = nm;
        }
        @Override
        public void run() {
            int i = 0;
            Random mRandom = new Random();
            int fi =  mRandom.nextInt(5);
            makeOnlyLog(nm+" : "+ Thread.currentThread().getName()+"\t upto id "+ fi);
            try {
                Thread.sleep(fi*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void finalize() {
            try {
                super.finalize();
                makeOnlyLog(nm+" : "+ Thread.currentThread().getName()+"\t FINALIZED");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void makeLog(String msg){
        Log.e("Thread Id "+Thread.currentThread().toString()," msg "+msg);
    }

    public void makeOnlyLog(String msg){
        Log.e("-> ","  "+msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}