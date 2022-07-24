package com.example.codengine.printerapp.PrinterEssentials.ThreadManager;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Manager {


    public static int CORE_POOL_SIZE = 5;
    public static int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors()+2;
    private static final int KEEP_ALIVE_TIME = 10;//idle thread

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
        //threadPoolExecutor.allowCoreThreadTimeOut(true);

    }

    public void runTask(Runnable runnable) {
        if(!threadPoolExecutor.isShutdown() || !threadPoolExecutor.isTerminating() || !threadPoolExecutor.isTerminated()){// && threadPoolExecutor.getActiveCount() != threadPoolExecutor.getMaximumPoolSize()){
            threadPoolExecutor.execute(runnable);
        } else {
            Log.e("THREAD POOL SHuTDOWN "+threadPoolExecutor.isShutdown(),"SO NEW THREAD");
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

    public String getStatus() {
        if (threadPoolExecutor!=null)
            return ("ACT: "+threadPoolExecutor.getActiveCount()+
                    "QUE: "+threadPoolExecutor.getQueue().size()+
                    "TSK: "+threadPoolExecutor.getTaskCount());

        else
            return "EMPTY";
    }
}
