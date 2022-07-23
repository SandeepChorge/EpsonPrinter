package com.example.codengine.printerapp.PrinterEssentials.ThreadManager;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Manager {


    public static int CORE_POOL_SIZE = 5;
    public static int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 20;//idle thread

    private static Manager managerInstance = null;

    //Queue for all the Tasks
    final BlockingQueue<Runnable> WorkQueue;
    private final ThreadPoolExecutor threadPoolExecutor;

    static {

        /*
        Static instance of Manager
         */
        managerInstance = new Manager();

    }

    /*
    Make sure Manager is a SingleTon Hence private;
     */

    private Manager() {

        WorkQueue = new LinkedBlockingQueue<Runnable>();
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, WorkQueue);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
     //   Log.e("Core thread timeout"+threadPoolExecutor.allowsCoreThreadTimeOut(),"is set");
    }

    public void runTask(Runnable runnable) {
        if(!threadPoolExecutor.isShutdown()){// && threadPoolExecutor.getActiveCount() != threadPoolExecutor.getMaximumPoolSize()){
            Log.e("THREAD POOL "+threadPoolExecutor.getActiveCount(),"NOT EXCEEDED MAX POOL SIZE SO EXECUTE");
            threadPoolExecutor.execute(runnable);
        } else {
            Log.e("THREAD POOL IS SHuTDOWN "+threadPoolExecutor.isShutdown(),"SO NEW THREAD");
            new Thread(runnable).start();
        }

        //threadPoolExecutor.execute(runnable);
    }

    public int getActivaionCount() {
        if (threadPoolExecutor!=null)
            return threadPoolExecutor.getActiveCount();
        else
            return 0;
    }

    public void ShutDownThreadPool(){
        if (threadPoolExecutor!=null){
            Log.e("THREAD POOL SHUTTING DOWN","YEP");
            threadPoolExecutor.shutdown();
        }
    }

    public static Manager getManagerInstance() {
        return managerInstance;
    }
}
